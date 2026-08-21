package com.example.assignment.database.remote.ChatBox

import com.example.assignment.ui.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ChatRepository(
    private val client: HttpClient = defaultClient()
) {
    suspend fun sendMessage(message: String): Result<LegalChatAnswer> {
        return when (val result = postChat(message)) {
            is Result.Success -> {
                val body = result.data
                val parsed = LegalChatAnswer.fromResponse(
                    reply = body.reply,
                    answer = body.answer,
                    statute = body.statute.ifBlank { body.legalBasis },
                    explanation = body.explanation,
                    nextSteps = body.nextSteps,
                    followUp = body.followUp
                )
                Result.Success(
                    if (parsed.answer.isBlank()) LegalChatAnswer.fromPlain(body.reply) else parsed
                )
            }
            is Result.Error -> result
        }
    }

    private suspend fun postChat(message: String): Result<ChatResponse> {
        return try {
            val response = client.post("${ChatConfig.baseUrl.trimEnd('/')}/chat") {
                contentType(ContentType.Application.Json)
                setBody(ChatRequest(message = message))
            }

            if (!response.status.isSuccess()) {
                val hint = when (response.status.value) {
                    404 -> " Check ChatConfig.baseUrl."
                    502, 503 -> " The chat server is starting or sleeping. Try again in a minute."
                    else -> ""
                }
                return Result.Error("Chat server error (${response.status.value}).$hint")
            }

            val body = response.body<ChatResponse>()
            if (body.reply.isBlank() && body.answer.isBlank() && body.explanation.isBlank()) {
                Result.Error("Empty reply from chat server.")
            } else {
                Result.Success(body)
            }
        } catch (error: Exception) {
            Result.Error(error.message ?: "Could not reach the chat server.")
        }
    }

    suspend fun validateDocument(text: String): Result<DocumentValidation> {
        val sample = text.trim()
        if (sample.length < 20) {
            return Result.Success(DocumentValidation.unreadable())
        }

        return try {
            val response = client.post("${ChatConfig.baseUrl.trimEnd('/')}/validate") {
                contentType(ContentType.Application.Json)
                setBody(ValidateRequest(text = sample.take(6000)))
            }

            if (response.status.value == 404 || response.status.value == 405) {
                return validateViaChat(sample)
            }

            if (!response.status.isSuccess()) {
                val hint = when (response.status.value) {
                    502, 503 -> " The chat server is starting or sleeping. Try again in a minute."
                    else -> ""
                }
                return Result.Error("Could not check this document (${response.status.value}).$hint")
            }

            Result.Success(response.body<DocumentValidation>().normalized())
        } catch (error: Exception) {
            when (val fallback = validateViaChat(sample)) {
                is Result.Success -> fallback
                is Result.Error -> Result.Error(
                    error.message ?: fallback.message
                )
            }
        }
    }

    private suspend fun validateViaChat(text: String): Result<DocumentValidation> {
        val prompt = """
Check this scanned document in TWO steps for a Malaysian employment app.

Return JSON only:
{
  "valid": true,
  "document_type": "employment contract",
  "summary": "why it is or is not a real employment document",
  "issues": [],
  "legal": true,
  "statute": "Employment Act 1955, Section 60",
  "legal_summary": "whether the terms fulfill Malaysian labour law",
  "violations": ["illegal clause"],
  "missing_requirements": ["mandatory term missing"],
  "next_steps": ["what the worker can do"]
}

Step 1 valid=true only if it is a real readable employment/HR/labour document.
Step 2 legal=true only if the CONTENT does not break Malaysian labour law and covers core required terms.
If valid=false, set legal=false and skip inventing violations.

document_type must be one of: employment contract, offer letter, payslip, warning letter, dismissal letter, other, unreadable.

Document:
${text.take(6000)}
        """.trimIndent()

        return when (val result = postChat(prompt)) {
            is Result.Success -> {
                val parsed = parseValidation(result.data.answer)
                    ?: parseValidation(result.data.reply)
                    ?: parseValidation(result.data.explanation)
                if (parsed != null) {
                    Result.Success(parsed.normalized())
                } else {
                    val raw = result.data.answer.ifBlank { result.data.reply }.trim()
                    Result.Success(
                        DocumentValidation(
                            valid = looksValid(raw),
                            documentType = "other",
                            summary = raw,
                            issues = emptyList()
                        ).normalized()
                    )
                }
            }
            is Result.Error -> result
        }
    }

    fun close() {
        client.close()
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }

        fun defaultClient(): HttpClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 120_000
            }
        }

        fun parseValidation(raw: String): DocumentValidation? {
            val cleaned = raw.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val start = cleaned.indexOf('{')
            val end = cleaned.lastIndexOf('}')
            if (start < 0 || end <= start) return null
            return runCatching {
                json.decodeFromString<DocumentValidation>(cleaned.substring(start, end + 1))
            }.getOrNull()
        }

        fun looksValid(raw: String): Boolean {
            val lowered = raw.lowercase()
            val invalidMarkers = listOf(
                "not valid",
                "invalid",
                "unreadable",
                "not an employment",
                "cannot check"
            )
            if (invalidMarkers.any { it in lowered }) return false
            return "valid" in lowered
        }
    }
}

@Serializable
private data class ChatRequest(
    val message: String
)

@Serializable
private data class ChatResponse(
    val reply: String = "",
    val answer: String = "",
    val statute: String = "",
    @SerialName("legal_basis") val legalBasis: String = "",
    val explanation: String = "",
    @SerialName("next_steps") val nextSteps: List<String> = emptyList(),
    @SerialName("follow_up") val followUp: String = "",
    val error: String? = null
)

@Serializable
private data class ValidateRequest(
    val text: String
)

@Serializable
data class DocumentValidation(
    val valid: Boolean,
    @SerialName("document_type") val documentType: String = "other",
    val summary: String = "",
    val issues: List<String> = emptyList(),
    val legal: Boolean? = null,
    @SerialName("legal_status") val legalStatus: String = "",
    val statute: String = "",
    @SerialName("legal_summary") val legalSummary: String = "",
    val violations: List<String> = emptyList(),
    @SerialName("missing_requirements") val missingRequirements: List<String> = emptyList(),
    @SerialName("next_steps") val nextSteps: List<String> = emptyList()
) {
    fun normalized(): DocumentValidation {
        val kind = documentType.trim().ifBlank { "other" }
        val unreadable = kind.equals("unreadable", ignoreCase = true)
        val isValid = valid && !unreadable
        val cleanIssues = issues.map { it.trim() }.filter { it.isNotEmpty() }
        val cleanViolations = violations.map { it.trim() }.filter { it.isNotEmpty() }
        val cleanMissing = missingRequirements.map { it.trim() }.filter { it.isNotEmpty() }
        val cleanSteps = nextSteps.map { it.trim() }.filter { it.isNotEmpty() }
        val isLegal = isValid && legal == true && cleanViolations.isEmpty()
            val status = when {
                !isValid -> "SKIPPED"
                isLegal -> "COMPLIANT"
                else -> "NON_COMPLIANT"
            }
        return copy(
            valid = isValid,
            documentType = kind,
            summary = summary.trim(),
            issues = cleanIssues,
            legal = isLegal,
            legalStatus = status,
            statute = statute.trim(),
            legalSummary = legalSummary.trim().ifBlank {
                if (!isValid) {
                    "Labour-law check was skipped because this is not a valid employment document."
                } else {
                    summary.trim()
                }
            },
            violations = cleanViolations,
            missingRequirements = cleanMissing,
            nextSteps = cleanSteps
        )
    }

    companion object {
        fun unreadable(): DocumentValidation = DocumentValidation(
            valid = false,
            documentType = "unreadable",
            summary = "Not enough readable text was found to check this document.",
            issues = listOf("The scan did not contain enough text."),
            legal = false,
            legalStatus = "SKIPPED",
            legalSummary = "Labour-law check was skipped because the document could not be read."
        )
    }
}
