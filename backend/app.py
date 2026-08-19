import hashlib
import json
import os
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path

import cohere
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS
from openai import OpenAI
from qdrant_client import QdrantClient
from qdrant_client.models import Fusion, FusionQuery, Prefetch, SparseVector

load_dotenv()

# -----------------------------
# CONFIG
# -----------------------------
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
QDRANT_URL = os.getenv("QDRANT_URL", "")
QDRANT_API_KEY = os.getenv("QDRANT_API_KEY", "")
COHERE_API_KEY = os.getenv("COHERE_API_KEY", "")

DEFAULT_COLLECTIONS = [
    "BASIC AI COMMUNICATION",
    "CHILDREN AND YOUNG PERSONS EMPLOYMENT ACT 1966",
    "EMPLOYEES MINIMUM STANDARDS OF HOUSING ACT 1990",
    "Employees Provident Fund (Amendment) Act 2025 (Act A1760) EPF",
    "Employees Social Security (Amendment) Act 2026 (Act A1788) SOCSO",
    "Employment Act 1955",
    "EMPLOYMENT INFORMATION ACT 1953",
    "EMPLOYMENT INSURANCE SYSTEM (AMENDMENT) ACT 2024",
    "Industrial Relations Act 1967 (Act 177)",
    "Labour Law Cap 67",
    "Labour Ordinance of Sabah (Amendment) Act 2025",
    "Labour Ordinance of Sarawak (Amendment) Act 2025",
    "Labour Ordinance of Sarawak Cap 76",
    "MINIMUM RETIREMENT AGE ACT 2012",
    "MINIMUM WAGES ORDER 2024",
    "NATIONAL WAGES CONSULTATIVE COUNCIL (AMENDMENT) ACT 2025",
    "PRIVATE EMPLOYMENT AGENCIES ACT 1981",
    "Occupational Safety and Health Act 1994 (Act 514) - 2024 Reprint",
]

COLLECTIONS_TO_SEARCH = [
    item.strip()
    for item in os.getenv("QDRANT_COLLECTIONS", "").split("|")
    if item.strip()
] or DEFAULT_COLLECTIONS


client = OpenAI(api_key=OPENAI_API_KEY)
qdrant = QdrantClient(url=QDRANT_URL, api_key=QDRANT_API_KEY, timeout=90.0)
cohere_client = None
if COHERE_API_KEY:
    cohere_client = cohere.ClientV2(api_key=COHERE_API_KEY)


# -----------------------------
# QDRANT SEARCH
# -----------------------------
def make_sparse_vector(query_text):
    words = query_text.lower().split()
    freq = Counter(words)
    indices = []
    values = []

    for word, count in freq.items():
        if len(word) > 2:
            index = int(hashlib.md5(word.encode()).hexdigest()[:8], 16)
            indices.append(index)
            values.append(float(count))

    return SparseVector(indices=indices, values=values)


def qdrant_payload_to_text(payload):
    return "\n".join(
        str(payload.get(field, ""))
        for field in (
            "law",
            "section",
            "section_title",
            "chapter",
            "amendment_title",
            "target_section",
            "category",
            "intent",
            "text",
        )
        if payload.get(field)
    )


def rerank_with_cohere(query_text, scored_hits, top_n):
    if not cohere_client or not scored_hits:
        return scored_hits[:top_n]

    documents = [qdrant_payload_to_text(item["payload"]) for item in scored_hits]

    try:
        rerank_response = cohere_client.rerank(
            model="rerank-v3.5",
            query=query_text,
            documents=documents,
            top_n=min(top_n, len(documents)),
        )
    except Exception as error:
        print(f"Cohere rerank failed, using Qdrant order instead: {error}")
        return scored_hits[:top_n]

    reranked_hits = []
    for result in rerank_response.results:
        item = scored_hits[result.index]
        reranked_hits.append(
            {
                **item,
                "rerank_score": result.relevance_score,
            }
        )

    return reranked_hits


COVERED_LAWS = [
    "Employment Act 1955",
    "Industrial Relations Act 1967",
    "Occupational Safety and Health Act 1994",
    "Children and Young Persons (Employment) Act 1966",
    "Minimum Wages Order 2024",
    "Minimum Retirement Age Act 2012",
    "Labour Ordinance (Sabah / Sarawak / Cap 67)",
    "EPF, SOCSO, and EIS amendment Acts",
    "Private employment agency rules",
]

QUERY_HINTS = (
    ("salary", "wages payment of wages Employment Act"),
    ("pay me", "wages payment of wages"),
    ("not paying", "wages due unpaid wages complaint Labour Department"),
    ("fired", "dismissal termination industrial relations unfair dismissal"),
    ("terminate", "termination dismissal notice"),
    ("resign", "resignation notice termination"),
    ("overtime", "hours of work overtime Employment Act"),
    ("leave", "annual leave sick leave maternity leave"),
    ("pregnant", "maternity protection Employment Act"),
    ("child", "children young persons employment"),
    ("safety", "occupational safety health OSHA"),
    ("minimum wage", "Minimum Wages Order 2024"),
    ("epf", "Employees Provident Fund"),
    ("socso", "Employees Social Security"),
    ("eis", "Employment Insurance System"),
)


def expand_query(question):
    lowered = question.lower()
    extras = [hint for keyword, hint in QUERY_HINTS if keyword in lowered]
    if not extras:
        return question
    return f"{question}\nRelated legal terms: {' '.join(extras)}"


def no_match_reply(user_question):
    covered = "\n".join(f"- {name}" for name in COVERED_LAWS)
    return (
        "I could not find a matching section in the legal database for that question.\n\n"
        f"Your question: \"{user_question.strip()}\"\n\n"
        "This chatbot only answers from Malaysian employment and labour statutes that were indexed. "
        "It covers:\n"
        f"{covered}\n\n"
        "Try asking again with more detail, for example:\n"
        "- what happened (unpaid wages, dismissal, overtime, leave, workplace safety)\n"
        "- who is involved (employee, employer, intern, foreign worker)\n"
        "- which state, if relevant (Peninsular Malaysia, Sabah, or Sarawak)\n\n"
        "If you rephrase around one of those topics, I can search the statutes again."
    )


def search_with_fallback(user_question):
    chunks = search_qdrant(user_question)
    if chunks:
        return chunks

    expanded = expand_query(user_question)
    if expanded != user_question:
        print(f"Retrying retrieval with expanded query: {expanded}")
        chunks = search_qdrant(expanded)
    return chunks


UNANSWERED_LOG = Path(__file__).resolve().parent / "unanswered.jsonl"
FAILURE_MARKERS = (
    "could not find",
    "cannot find",
    "i cannot find",
    "no matching section",
    "not in the legal database",
    "provided law chunks",
    "no text content available",
)


def looks_like_unanswered(reply):
    text = (reply or "").lower()
    return any(marker in text for marker in FAILURE_MARKERS)


def track_unanswered(question, reason, reply=""):
    record = {
        "time": datetime.now(timezone.utc).isoformat(),
        "question": question,
        "reason": reason,
        "reply": (reply or "")[:500],
    }
    with UNANSWERED_LOG.open("a", encoding="utf-8") as file:
        file.write(json.dumps(record, ensure_ascii=False) + "\n")
    print(f"Tracked unanswered question ({reason}): {question}")


def rewrite_search_queries(question):
    try:
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {
                    "role": "system",
                    "content": (
                        "Rewrite the user question into 3 short Malaysian employment-law search queries. "
                        "Use statute keywords such as wages, dismissal, overtime, OSHA, EPF, SOCSO, "
                        "children, Sabah, Sarawak, Employment Act. "
                        "Return a JSON array of strings only."
                    ),
                },
                {"role": "user", "content": question},
            ],
            temperature=0.2,
        )
        raw = (response.choices[0].message.content or "").strip()
        if raw.startswith("```"):
            raw = raw.strip("`")
            raw = raw.replace("json", "", 1).strip()
        queries = json.loads(raw)
        return [item.strip() for item in queries if isinstance(item, str) and item.strip()][:3]
    except Exception as error:
        print(f"Query rewrite failed: {error}")
        expanded = expand_query(question)
        return [expanded] if expanded != question else []


def chunk_key(payload):
    return (
        str(payload.get("id") or payload.get("chunk_id") or ""),
        str(payload.get("law") or ""),
        str(payload.get("text") or "")[:120],
    )


def merge_chunks(*groups):
    seen = set()
    merged = []
    for group in groups:
        for payload in group:
            key = chunk_key(payload)
            if key in seen:
                continue
            seen.add(key)
            merged.append(payload)
    return merged


def improve_retrieval(question, existing=None):
    chunks = list(existing or [])
    for query in rewrite_search_queries(question):
        print(f"Improving retrieval with rewritten query: {query}")
        chunks = merge_chunks(chunks, search_qdrant(query))
    return chunks


def chunks_to_context(payloads):
    context_str = ""
    for payload in payloads:
        law = payload.get(
            "law",
            payload.get("category", payload.get("_source_name", "Unknown Law")),
        )
        section = payload.get(
            "section",
            payload.get(
                "intent",
                payload.get("target_section", payload.get("chapter", "Unknown")),
            ),
        )
        context_str += f"\n--- Source Statute: {law} (Section {section}) ---\n"
        context_str += f"{payload.get('text') or qdrant_payload_to_text(payload) or 'No text content available.'}\n"
    return context_str


def generate_answer(user_question, context_str):
    system_prompt = (
        "You are an expert Malaysian legal advisor specializing in employment and labor statutes.\n"
        "Use ONLY the provided text context below. Do not invent sections or penalties.\n"
        "Always cite the Source Statute and Section from the context when explaining rules.\n"
        "If the context is only partly related:\n"
        "- First say what the retrieved statutes DO cover that is closest to the question.\n"
        "- Then say what is still missing.\n"
        "- Ask 1 short clarifying question so the user can rephrase.\n"
        "- Suggest which statute area to ask about next (wages, dismissal, OSHA, children, Sabah/Sarawak, EPF/SOCSO).\n"
        "Do not reply with only 'I cannot find that specific information in the provided law chunks.'\n"
        "Never give a dead-end answer. Always offer a next step.\n\n"
        f"CONTEXT:\n{context_str}"
    )
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_question},
        ],
        temperature=0.2,
    )
    return response.choices[0].message.content


# -----------------------------
# CHAT GENERATION WITH CONTEXT
# -----------------------------
def ask_legal_bot(user_question):
    relevant_chunks = search_with_fallback(user_question)

    if not relevant_chunks:
        print("No chunks on first search. Improving retrieval immediately.")
        relevant_chunks = improve_retrieval(user_question)

    if not relevant_chunks:
        track_unanswered(user_question, "no_chunks")
        return no_match_reply(user_question)

    print(f"Found {len(relevant_chunks)} reranked Qdrant matches.")
    reply = generate_answer(user_question, chunks_to_context(relevant_chunks))

    if looks_like_unanswered(reply):
        print("Answer looked incomplete. Improving retrieval and regenerating.")
        improved_chunks = improve_retrieval(user_question, relevant_chunks)
        if improved_chunks:
            improved_reply = generate_answer(user_question, chunks_to_context(improved_chunks))
            if not looks_like_unanswered(improved_reply):
                return improved_reply
            reply = improved_reply
        track_unanswered(user_question, "weak_answer", reply)

    return reply


def search_qdrant(
    query_text,
    limit_per_collection=6,
    rerank_candidates=40,
    global_context_max=8,
):
    dense_vector = client.embeddings.create(
        model="text-embedding-3-small",
        input=query_text,
    ).data[0].embedding
    sparse_vector = make_sparse_vector(query_text)

    try:
        existing_collections = {c.name for c in qdrant.get_collections().collections}
    except Exception as error:
        print(f"Connection error fetching Qdrant collection list: {error}")
        return []

    scored_hits = []

    for collection_name in COLLECTIONS_TO_SEARCH:
        if collection_name not in existing_collections:
            continue

        try:
            results = qdrant.query_points(
                collection_name=collection_name,
                prefetch=[
                    Prefetch(query=sparse_vector, using="sparse", limit=limit_per_collection),
                    Prefetch(query=dense_vector, using="dense", limit=limit_per_collection),
                ],
                query=FusionQuery(fusion=Fusion.RRF),
                limit=limit_per_collection,
                with_payload=True,
            )

            for hit in results.points:
                scored_hits.append(
                    {
                        "score": hit.score if hit.score is not None else 0.0,
                        "payload": hit.payload or {},
                    }
                )

        except Exception as error:
            print(f"Skipping Qdrant collection '{collection_name}': {error}")

    scored_hits.sort(key=lambda item: item["score"], reverse=True)
    scored_hits = scored_hits[:rerank_candidates]
    scored_hits = rerank_with_cohere(query_text, scored_hits, global_context_max)

    return [item["payload"] for item in scored_hits]


# -----------------------------
# FLASK API
# -----------------------------
app = Flask(__name__)
CORS(app)


@app.get("/")
def home():
    return jsonify({
        "ok": True,
        "service": "GHRC legal chat",
        "try": [
            "GET /health",
            "GET /chat?message=if company is not paying me my salary, what can I do?",
            "POST /chat with JSON {\"message\": \"...\"}",
        ],
    })


@app.get("/health")
def health():
    return jsonify({"ok": True})


@app.get("/unanswered")
def unanswered():
    if not UNANSWERED_LOG.exists():
        return jsonify({"count": 0, "items": []})

    items = []
    for line in UNANSWERED_LOG.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line:
            continue
        try:
            items.append(json.loads(line))
        except json.JSONDecodeError:
            continue
    return jsonify({"count": len(items), "items": items[-50:]})


@app.route("/chat", methods=["GET", "POST"])
def chat():
    if request.method == "GET":
        message = (request.args.get("message") or request.args.get("question") or "").strip()
    else:
        data = request.get_json(silent=True) or {}
        message = (data.get("message") or data.get("question") or "").strip()

    if not message:
        return jsonify({"error": "message is required"}), 400

    try:
        reply = ask_legal_bot(message)
        return jsonify({"reply": reply})
    except Exception as error:
        print(f"Chat request failed: {error}")
        return jsonify({"error": str(error)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.getenv("PORT", "5000")), debug=True)
