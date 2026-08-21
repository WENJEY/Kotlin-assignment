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


def no_match_payload(user_question):
    covered = "; ".join(COVERED_LAWS)
    payload = {
        "answer": "I could not find a matching section in the indexed Malaysian employment statutes.",
        "statute": "",
        "explanation": (
            f'Your question: "{user_question.strip()}". '
            "This chatbot only answers from Malaysian employment and labour statutes that were indexed. "
            f"It covers: {covered}."
        ),
        "next_steps": [
            "Say what happened (unpaid wages, dismissal, overtime, leave, or workplace safety).",
            "Say who is involved (employee, employer, intern, or foreign worker).",
            "Mention the state if relevant (Peninsular Malaysia, Sabah, or Sarawak).",
        ],
        "follow_up": "Which topic is closest: wages, dismissal, overtime, leave, or workplace safety?",
    }
    payload["reply"] = format_structured_reply(payload)
    return payload


def format_structured_reply(payload):
    parts = []
    answer = str(payload.get("answer") or "").strip()
    statute = str(payload.get("statute") or "").strip()
    explanation = str(payload.get("explanation") or "").strip()
    follow_up = str(payload.get("follow_up") or "").strip()
    steps = payload.get("next_steps") or []
    if not isinstance(steps, list):
        steps = [str(steps)]

    if answer:
        parts.append(f"Answer:\n{answer}")
    if statute:
        parts.append(f"Legal basis:\n{statute}")
    if explanation:
        parts.append(f"Explanation:\n{explanation}")
    bullets = [f"- {str(item).strip()}" for item in steps if str(item).strip()]
    if bullets:
        parts.append("What you can do:\n" + "\n".join(bullets))
    if follow_up:
        parts.append(f"Next question:\n{follow_up}")
    return "\n\n".join(parts)


def normalize_chat_payload(raw, fallback_text=""):
    data = raw if isinstance(raw, dict) else {}
    steps = data.get("next_steps") or []
    if not isinstance(steps, list):
        steps = [str(steps)]
    payload = {
        "answer": str(data.get("answer") or "").strip(),
        "statute": str(data.get("statute") or data.get("legal_basis") or "").strip(),
        "explanation": str(data.get("explanation") or "").strip(),
        "next_steps": [str(item).strip() for item in steps if str(item).strip()][:4],
        "follow_up": str(data.get("follow_up") or "").strip(),
    }
    if not payload["answer"]:
        payload["answer"] = (fallback_text or "").strip()
    if not payload["answer"] and not payload["explanation"]:
        payload["answer"] = "I could not produce a clear answer from the statute database."
    payload["reply"] = format_structured_reply(payload)
    return payload


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
        "Always cite the Source Statute and Section from the context.\n"
        "If the context is only partly related:\n"
        "- Explain what the retrieved statutes DO cover that is closest to the question.\n"
        "- Say what is still missing in explanation.\n"
        "- Put one clarifying question in follow_up.\n"
        "Never give a dead-end answer. Always offer a next step.\n"
        "Return JSON only with this shape:\n"
        "{"
        '"answer": "1-3 sentence direct answer", '
        '"statute": "Law name and section, e.g. Employment Act 1955, Section 60", '
        '"explanation": "2-5 sentences from the context", '
        '"next_steps": ["practical step 1", "practical step 2"], '
        '"follow_up": "one short clarifying question, or empty string"'
        "}\n"
        "next_steps must have 2 to 4 short actions the worker can take.\n"
        "follow_up must be empty if the question is already clear.\n\n"
        f"CONTEXT:\n{context_str}"
    )
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_question},
        ],
        temperature=0.2,
        response_format={"type": "json_object"},
    )
    raw_text = response.choices[0].message.content or ""
    parsed = parse_json_object(raw_text) or {}
    return normalize_chat_payload(parsed, fallback_text=raw_text)


def payload_looks_unanswered(payload):
    text = " ".join(
        [
            str(payload.get("answer") or ""),
            str(payload.get("explanation") or ""),
            str(payload.get("reply") or ""),
        ]
    )
    return looks_like_unanswered(text)


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
        return no_match_payload(user_question)

    print(f"Found {len(relevant_chunks)} reranked Qdrant matches.")
    payload = generate_answer(user_question, chunks_to_context(relevant_chunks))

    if payload_looks_unanswered(payload):
        print("Answer looked incomplete. Improving retrieval and regenerating.")
        improved_chunks = improve_retrieval(user_question, relevant_chunks)
        if improved_chunks:
            improved_payload = generate_answer(user_question, chunks_to_context(improved_chunks))
            if not payload_looks_unanswered(improved_payload):
                return improved_payload
            payload = improved_payload
        track_unanswered(user_question, "weak_answer", payload.get("reply", ""))

    return payload


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


def parse_json_object(raw):
    text = (raw or "").strip()
    if text.startswith("```"):
        text = text.strip("`")
        text = text.replace("json", "", 1).strip()
    start = text.find("{")
    end = text.rfind("}")
    if start < 0 or end <= start:
        return None
    try:
        parsed = json.loads(text[start : end + 1])
    except json.JSONDecodeError:
        return None
    return parsed if isinstance(parsed, dict) else None


def validate_scanned_document(document_text):
    sample = (document_text or "").strip()[:6000]
    if len(sample) < 20:
        return unreadable_validation_payload()

    identity = classify_scanned_document(sample)
    if not identity["valid"]:
        return {
            **identity,
            "legal": False,
            "legal_status": "SKIPPED",
            "statute": "",
            "legal_summary": "Labour-law check was skipped because this is not a valid employment document.",
            "violations": [],
            "missing_requirements": [],
            "next_steps": [],
        }

    legal = check_labour_compliance(sample, identity["document_type"])
    return {**identity, **legal}


def unreadable_validation_payload():
    return {
        "valid": False,
        "document_type": "unreadable",
        "summary": "Not enough readable text was found to check this document.",
        "issues": ["The scan did not contain enough text."],
        "legal": False,
        "legal_status": "SKIPPED",
        "statute": "",
        "legal_summary": "Labour-law check was skipped because the document could not be read.",
        "violations": [],
        "missing_requirements": [],
        "next_steps": [],
    }


def classify_scanned_document(sample):
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "system",
                "content": (
                    "You only decide if a scanned file is a real Malaysian employment/HR document.\n"
                    "Do not judge whether the terms are legal yet.\n"
                    "Return JSON only:\n"
                    "{"
                    '"valid": true or false, '
                    '"document_type": "employment contract|offer letter|payslip|warning letter|'
                    'dismissal letter|other|unreadable", '
                    '"summary": "1-3 sentences", '
                    '"issues": ["why it is not a valid employment document"]'
                    "}\n"
                    "valid=true only if it is a genuine employment, HR, or labour document with readable terms.\n"
                    "valid=false if it is unreadable, a random photo, a non-employment file, or too incomplete to identify."
                ),
            },
            {"role": "user", "content": f"Classify this scanned document:\n\n{sample}"},
        ],
        temperature=0.1,
        response_format={"type": "json_object"},
    )
    parsed = parse_json_object(response.choices[0].message.content) or {}
    issues = parsed.get("issues") or []
    if not isinstance(issues, list):
        issues = [str(issues)]
    document_type = str(parsed.get("document_type") or "other").strip() or "other"
    summary = str(parsed.get("summary") or "").strip()
    valid = bool(parsed.get("valid")) and document_type != "unreadable"
    if not summary:
        summary = "The AI could not identify this as an employment document."
        valid = False
    return {
        "valid": valid,
        "document_type": document_type,
        "summary": summary,
        "issues": [str(item).strip() for item in issues if str(item).strip()],
    }


def check_labour_compliance(sample, document_type):
    query = f"{document_type} Malaysian employment law required terms {sample[:800]}"
    chunks = search_with_fallback(query)
    if not chunks:
        chunks = improve_retrieval(query)
    context = chunks_to_context(chunks) if chunks else "No matching statute chunks were found."

    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "system",
                "content": (
                    "You check whether the CONTENT of a scanned Malaysian employment document "
                    "is legal and fulfills Malaysian labour law.\n"
                    "Use ONLY the statute context below. Do not invent sections or penalties.\n"
                    f"Document type: {document_type}\n"
                    "Check wages, hours of work, rest days, leave, notice, dismissal, deductions, "
                    "children/young persons, OSHA, EPF/SOCSO/EIS, and any other term in the text.\n"
                    "Return JSON only:\n"
                    "{"
                    '"legal": true or false, '
                    '"statute": "Law name and section(s) from the context", '
                    '"legal_summary": "2-5 sentences on whether the terms fulfill the law", '
                    '"violations": ["term that appears to break the law"], '
                    '"missing_requirements": ["mandatory term that is missing"], '
                    '"next_steps": ["what the worker can do"]'
                    "}\n"
                    "Set legal=true only if there is no clear illegal clause AND the document covers "
                    "the core required terms for this document type under the provided statutes.\n"
                    "Set legal=false if wages/hours are below the law, clauses are illegal, "
                    "mandatory terms are missing, or compliance cannot be shown from the context.\n\n"
                    f"STATUTE CONTEXT:\n{context}"
                ),
            },
            {"role": "user", "content": f"Check this document against Malaysian labour law:\n\n{sample}"},
        ],
        temperature=0.1,
        response_format={"type": "json_object"},
    )
    parsed = parse_json_object(response.choices[0].message.content) or {}
    violations = parsed.get("violations") or []
    missing = parsed.get("missing_requirements") or []
    steps = parsed.get("next_steps") or []
    if not isinstance(violations, list):
        violations = [str(violations)]
    if not isinstance(missing, list):
        missing = [str(missing)]
    if not isinstance(steps, list):
        steps = [str(steps)]
    legal = bool(parsed.get("legal")) and not violations
    summary = str(parsed.get("legal_summary") or "").strip()
    if not summary:
        summary = "The AI could not finish the Malaysian labour-law check."
        legal = False
    return {
        "legal": legal,
        "legal_status": "COMPLIANT" if legal else "NON_COMPLIANT",
        "statute": str(parsed.get("statute") or "").strip(),
        "legal_summary": summary,
        "violations": [str(item).strip() for item in violations if str(item).strip()],
        "missing_requirements": [str(item).strip() for item in missing if str(item).strip()],
        "next_steps": [str(item).strip() for item in steps if str(item).strip()][:4],
    }


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
            "POST /validate with JSON {\"text\": \"scanned document text\"}",
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
        payload = ask_legal_bot(message)
        if not isinstance(payload, dict):
            payload = normalize_chat_payload({}, fallback_text=str(payload))
        return jsonify(payload)
    except Exception as error:
        print(f"Chat request failed: {error}")
        return jsonify({"error": str(error)}), 500


@app.post("/validate")
def validate_document():
    data = request.get_json(silent=True) or {}
    text = (data.get("text") or data.get("document") or "").strip()
    if not text:
        return jsonify({"error": "text is required"}), 400

    try:
        return jsonify(validate_scanned_document(text))
    except Exception as error:
        print(f"Validate request failed: {error}")
        return jsonify({"error": str(error)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.getenv("PORT", "5000")), debug=True)
