import os
from pathlib import Path

from dotenv import load_dotenv
from qdrant_client import QdrantClient

load_dotenv(Path(__file__).resolve().parent / ".env")

COLLECTION = "BASIC AI COMMUNICATION"

client = QdrantClient(
    url=os.getenv("QDRANT_URL"),
    api_key=os.getenv("QDRANT_API_KEY"),
    timeout=90.0,
)

updated = 0
next_offset = None

while True:
    points, next_offset = client.scroll(
        collection_name=COLLECTION,
        limit=32,
        offset=next_offset,
        with_payload=True,
        with_vectors=False,
    )
    if not points:
        break

    for point in points:
        payload = point.payload or {}
        category = str(payload.get("category") or "Basic AI Communication")
        intent = str(payload.get("intent") or point.id)
        examples = payload.get("examples") or []
        responses = payload.get("responses") or []
        text = str(payload.get("text") or "").strip()
        if not text:
            text = (
                f"Category: {category}\n"
                f"Intent: {intent}\n"
                f"User examples: {'; '.join(str(item) for item in examples)}\n"
                f"AI responses: {'; '.join(str(item) for item in responses)}"
            ).strip()

        client.set_payload(
            collection_name=COLLECTION,
            payload={
                "id": point.id,
                "law": "BASIC AI COMMUNICATION",
                "section": intent,
                "section_title": category,
                "text": text,
            },
            points=[point.id],
        )
        updated += 1

    if next_offset is None:
        break

print(f"Updated {updated} points in {COLLECTION}")
