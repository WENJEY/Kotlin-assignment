package com.example.assignment.database.remote

import com.example.assignment.ui.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ChatRepository(
    private val client: HttpClient = defaultClient()
) {
    suspend fun sendMessage(message: String): Result<String> {
        return try {
            val response = client.post("${ChatConfig.baseUrl.trimEnd('/')}/chat") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "1")
                setBody(ChatRequest(message = message))
            }

            if (!response.status.isSuccess()) {
                val hint = when (response.status.value) {
                    404 -> " Start Flask and ngrok, then update ChatConfig.baseUrl."
                    502, 503 -> " Flask is not running behind ngrok."
                    else -> ""
                }
                return Result.Error("Chat server error (${response.status.value}).$hint")
            }

            val reply = response.body<ChatResponse>().reply.trim()
            if (reply.isEmpty()) {
                Result.Error("Empty reply from chat server.")
            } else {
                Result.Success(reply)
            }
        } catch (error: Exception) {
            Result.Error(error.message ?: "Could not reach the chat server.")
        }
    }

    fun close() {
        client.close()
    }

    private companion object {
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
    }
}

@Serializable
private data class ChatRequest(
    val message: String
)

@Serializable
private data class ChatResponse(
    val reply: String = "",
    val error: String? = null
)
