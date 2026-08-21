package com.example.assignment.database.remote.ChatBox

data class LegalChatAnswer(
    val answer: String,
    val statute: String = "",
    val explanation: String = "",
    val nextSteps: List<String> = emptyList(),
    val followUp: String = ""
) {
    val hasStructure: Boolean
        get() = statute.isNotBlank() ||
            explanation.isNotBlank() ||
            nextSteps.isNotEmpty() ||
            followUp.isNotBlank()

    val displayText: String
        get() = buildString {
            if (answer.isNotBlank()) {
                append("Answer:\n").append(answer.trim())
            }
            if (statute.isNotBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("Legal basis:\n").append(statute.trim())
            }
            if (explanation.isNotBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("Explanation:\n").append(explanation.trim())
            }
            if (nextSteps.isNotEmpty()) {
                if (isNotEmpty()) append("\n\n")
                append("What you can do:\n")
                append(nextSteps.joinToString("\n") { "- $it" })
            }
            if (followUp.isNotBlank()) {
                if (isNotEmpty()) append("\n\n")
                append("Next question:\n").append(followUp.trim())
            }
        }.ifBlank { answer }

    companion object {
        fun fromPlain(text: String): LegalChatAnswer = parse(text)

        fun fromResponse(
            reply: String,
            answer: String = "",
            statute: String = "",
            explanation: String = "",
            nextSteps: List<String> = emptyList(),
            followUp: String = ""
        ): LegalChatAnswer {
            if (answer.isNotBlank() || explanation.isNotBlank() || nextSteps.isNotEmpty() || followUp.isNotBlank()) {
                return LegalChatAnswer(
                    answer = answer.ifBlank { reply.trim() },
                    statute = statute,
                    explanation = explanation,
                    nextSteps = nextSteps.map { it.trim() }.filter { it.isNotEmpty() },
                    followUp = followUp
                )
            }
            return parse(reply)
        }

        fun parse(text: String): LegalChatAnswer {
            val cleaned = text.trim()
            if (cleaned.isEmpty()) return LegalChatAnswer(answer = "")

            val sections = linkedMapOf<String, StringBuilder>()
            var current: String? = null
            val header = Regex(
                "^(Answer|Legal basis|Explanation|What you can do|Next question)\\s*:\\s*(.*)$",
                RegexOption.IGNORE_CASE
            )
            cleaned.lineSequence().forEach { rawLine ->
                val line = rawLine.trimEnd()
                val match = header.matchEntire(line.trim())
                if (match != null) {
                    current = match.groupValues[1].lowercase()
                    val block = sections.getOrPut(current!!) { StringBuilder() }
                    val rest = match.groupValues[2].trim()
                    if (rest.isNotEmpty()) block.append(rest)
                } else if (current != null) {
                    val block = sections.getValue(current!!)
                    if (block.isNotEmpty()) block.append('\n')
                    block.append(line.trimEnd())
                }
            }

            if (sections.isEmpty()) {
                return LegalChatAnswer(answer = cleaned)
            }

            val steps = sections["what you can do"]?.toString().orEmpty()
                .lineSequence()
                .map { it.trim().removePrefix("- ").removePrefix("• ").trim() }
                .filter { it.isNotEmpty() }
                .toList()

            return LegalChatAnswer(
                answer = sections["answer"]?.toString()?.trim().orEmpty().ifBlank { cleaned },
                statute = sections["legal basis"]?.toString()?.trim().orEmpty(),
                explanation = sections["explanation"]?.toString()?.trim().orEmpty(),
                nextSteps = steps,
                followUp = sections["next question"]?.toString()?.trim().orEmpty()
            )
        }
    }
}
