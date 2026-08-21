package com.example.assignment.database.remote.supabase

import android.util.Log
import com.example.assignment.database.remote.ChatBox.ChatConversation
import com.example.assignment.database.remote.ChatBox.ChatHistoryMessage
import com.example.assignment.database.remote.Data.Feedback
import com.example.assignment.database.remote.Data.RemoteScannedDocument
import com.example.assignment.database.remote.Repository
import com.example.assignment.database.remote.Data.User
import com.example.assignment.database.remote.supabase.SupabaseClientProvider.client
import com.example.assignment.ui.utils.Result
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.UUID

class SupabaseRepository : Repository {
    private val auth get() = client.auth

    override fun isLoggedIn(): Boolean = auth.currentUserOrNull() != null

    override fun currentUser(): User? = auth.currentUserOrNull()?.let { user ->
        User(
            id = user.id,
            email = user.email.orEmpty(),
            username = user.userMetadata?.get("username")?.jsonPrimitive?.content.orEmpty()
        )
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getUserProfile(): Result<User> = runCatching {
        val authUser = client.auth.currentUserOrNull() ?: error("Not logged in")
        val usernameFromAuth = authUser.userMetadata?.get("username")?.jsonPrimitive?.content.orEmpty()
        val emailFromAuth = authUser.email.orEmpty()

        val row = client.from("profiles").select {
            filter { eq("id", authUser.id) }
        }.decodeList<ProfileRow>().firstOrNull()

        User(
            id = authUser.id,
            email = row?.email?.takeIf { it.isNotBlank() } ?: emailFromAuth,
            username = row?.username?.takeIf { it.isNotBlank() } ?: usernameFromAuth,
            age = row?.age ?: 0,
            phoneNumber = row?.phoneNumber.orEmpty(),
            gender = row?.gender.orEmpty(),
            profileImageUrl = row?.profileImageUrl
        )
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Load user profile failed", exception)
            Result.Error("Unable to load user profile")
        }
    )

    override suspend fun updateUserProfile(
        username: String,
        age: Int?,
        phoneNumber: String,
        gender: String
    ): Result<Unit> = runCatching {
        val authUser = client.auth.currentUserOrNull() ?: error("Not logged in")
        val email = authUser.email?.takeIf { it.isNotBlank() }
            ?: error("Email is missing from your account")

        client.from("profiles").upsert(
            ProfileUpsert(
                id = authUser.id,
                username = username.trim(),
                email = email,
                age = age,
                phoneNumber = phoneNumber.trim().ifBlank { null },
                gender = gender.trim().ifBlank { null }
            )
        )
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Update user profile failed", exception)
            Result.Error(
                exception.message
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unable to save user profile"
            )
        }
    )

    override suspend fun getEmailByUsername(username: String): Result<String> = try {
        val email = client.postgrest
            .rpc(
                function = "login_email_for_username",
                parameters = buildJsonObject { put("login_username", username.trim()) }
            )
            .decodeAs<String>()

        Result.Success(email)
    } catch (e: Exception) {
        Result.Error("Invalid username/email or password")
    }

    override suspend fun login(email: String, password: String): Result<User> = try {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        currentUser()?.let { Result.Success(it) } ?: Result.Error("Login failed")
    } catch (e: Exception) {
        Result.Error("Invalid username/email or password")
    }

    override suspend fun signUp(username: String, email: String, password: String): Result<User> = try {
        val user= auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject { put("username", username) }
        }

        if (user != null) {
            Result.Success(
                User(
                    id = user.id,
                    email = user.email.orEmpty(),
                    username = user.userMetadata?.get("username")?.jsonPrimitive?.content.orEmpty()
                )
            )
        } else {
            Result.Success(User(email = email, username = username))
        }
    } catch (e: Exception) {
        Result.Error(mapSupabaseError(e))
    }

    private fun mapSupabaseError(exception: Exception): String {
        val message = exception.message.orEmpty()
        Log.e("AuthError", "Raw error: $message") // Log full error for debugging

        return when {
            message.contains("already registered", ignoreCase = true) ->
                "This email is already registered"
            message.contains("weak password", ignoreCase = true) ||
                    message.contains("at least 6 characters", ignoreCase = true) ->
                "Password is too weak. Use at least 6 characters."
            message.contains("invalid format", ignoreCase = true) ||
                    message.contains("valid email", ignoreCase = true) ->
                "Please enter a valid email address"
            message.contains("rate limit", ignoreCase = true) ->
                "Too many attempts. Please try again later."
            message.contains("email not confirmed", ignoreCase = true) ->
                "Please confirm your email before logging in"
            else -> "Something went wrong. Please try again."
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {

        return try {

            auth.resetPasswordForEmail(
                email = email.trim(),
                redirectUrl = null
            )

            Result.Success(Unit)

        } catch (e: Exception) {

            Log.e(
                "SupabaseRepository",
                "Password reset failed",
                e
            )

            Result.Error(
                mapResetPasswordError(e)
            )
        }
    }
    private fun mapResetPasswordError(exception: Exception): String {
        val message = listOfNotNull(exception.message, exception.cause?.message)
            .joinToString(" ")

        Log.e(
            "SupabaseRepository",
            "Raw password reset error: $message"
        )

        return when {
            message.contains("rate limit", ignoreCase = true) ||
                (message.contains("after", ignoreCase = true) &&
                    message.contains("seconds", ignoreCase = true)) ->
                "Too many attempts. Please try again later."
            message.contains("redirect", ignoreCase = true) ->
                "Redirect URL is not allowed. Keep Site URL as http://localhost:3000 in Authentication > URL Configuration."
            message.contains("unexpected_failure", ignoreCase = true) ||
                message.contains("error sending recovery email", ignoreCase = true) ->
                "Unable to send the verification email. Check SMTP and Reset Password template settings."
            message.contains("invalid email", ignoreCase = true) ||
                message.contains("valid email", ignoreCase = true) ->
                "Please enter a valid email address."
            else -> "Unable to send verification code. Please try again."
        }
    }

    override suspend fun uploadProfileImage(userId: String, imageBytes: ByteArray): Result<String> = runCatching {
        require(imageBytes.isNotEmpty()) { "The selected image is empty" }
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        require(user.id == userId) { "You can only update your own profile image" }
        val path = "$userId/avatar.jpg"
        client.storage.from("avatars").upload(path, imageBytes) {
            upsert = true
            contentType = ContentType.Image.JPEG
        }
        // Same storage path is reused; version query forces every device to fetch the new file.
        val publicUrl = client.storage.from("avatars").publicUrl(path)
        "$publicUrl?v=${System.currentTimeMillis()}"
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Profile image upload failed", exception)
            Result.Error(profileImageError("upload", exception))
        }
    )

    override suspend fun updateProfileImage(
        imageUrl: String
    ): Result<Unit> = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: error("Not logged in")

        val username = user.userMetadata?.get("username")?.jsonPrimitive?.content
            ?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore('@')?.takeIf { it.isNotBlank() }
            ?: error("Username is missing from your account")
        val email = user.email?.takeIf { it.isNotBlank() }
            ?: error("Email is missing from your account")

        // Upsert so the URL is saved even if the profiles row was never created.
        // profiles.username and profiles.email are NOT NULL.
        client.from("profiles").upsert(
            ProfileImageUpsert(
                id = user.id,
                username = username,
                email = email,
                profileImageUrl = imageUrl
            )
        )
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Profile image URL update failed", exception)
            Result.Error(profileImageError("save", exception))
        }
    )

    override suspend fun getProfileImageUrl(): Result<String?> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        client.from("profiles").select {
            filter { eq("id", user.id) }
        }.decodeList<ProfileImage>().firstOrNull()?.url
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Profile image load failed", exception)
            Result.Error("Unable to load profile image")
        }
    )

    override suspend fun submitFeedback(feedback: Feedback): Result<Unit> = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: error("Please sign in again to send feedback")

        client.from("feedback").insert(
            FeedbackInsert(
                userId = user.id,
                rating = feedback.rating,
                category = feedback.category,
                message = feedback.message.trim(),
                contactEmail = feedback.contactEmail?.trim()?.ifBlank { null }
            )
        )
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Submit feedback failed", exception)
            val detail = exception.message.orEmpty()
            Result.Error(
                when {
                    detail.contains("relation", ignoreCase = true) &&
                        detail.contains("feedback", ignoreCase = true) ->
                        "Feedback storage is not ready. Run supabase/feedback_setup.sql."
                    detail.contains("row-level security", ignoreCase = true) ||
                        detail.contains("permission denied", ignoreCase = true) ||
                        detail.contains("42501") ->
                        "Permission denied. Run supabase/feedback_setup.sql."
                    else -> "Unable to send feedback. Please try again."
                }
            )
        }
    )

    override suspend fun loadChatConversations(): Result<List<ChatConversation>> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        client.from("chat_conversations").select {
            filter { eq("user_id", user.id) }
            order("updated_at", Order.DESCENDING)
        }.decodeList<ConversationRow>().map { row ->
            ChatConversation(id = row.id, title = row.title, updatedAt = row.updatedAt)
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Load chat conversations failed", exception)
            Result.Error(chatHistoryError("load", exception))
        }
    )

    override suspend fun createChatConversation(title: String): Result<ChatConversation> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        val conversation = ChatConversation(
            id = UUID.randomUUID().toString(),
            title = title.trim().ifBlank { "New chat" }.take(60)
        )
        client.from("chat_conversations").insert(
            ConversationInsert(
                id = conversation.id,
                userId = user.id,
                title = conversation.title
            )
        )
        conversation
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Create chat conversation failed", exception)
            Result.Error(chatHistoryError("save", exception))
        }
    )

    override suspend fun loadChatHistory(conversationId: String): Result<List<ChatHistoryMessage>> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        client.from("chat_messages").select {
            filter {
                eq("user_id", user.id)
                eq("conversation_id", conversationId)
            }
            order("created_at", Order.ASCENDING)
        }.decodeList<ChatMessageRow>().map { row ->
            ChatHistoryMessage(
                id = row.id,
                text = row.text,
                isFromUser = row.isFromUser
            )
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Load chat history failed", exception)
            Result.Error(chatHistoryError("load", exception))
        }
    )

    override suspend fun saveChatMessage(
        conversationId: String,
        text: String,
        isFromUser: Boolean
    ): Result<Unit> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        client.from("chat_messages").insert(
            ChatMessageInsert(
                userId = user.id,
                conversationId = conversationId,
                text = text,
                isFromUser = isFromUser
            )
        )
        client.from("chat_conversations").update(
            ConversationTouch(updatedAt = Instant.now().toString())
        ) {
            filter { eq("id", conversationId) }
        }
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Save chat message failed", exception)
            Result.Error(chatHistoryError("save", exception))
        }
    )

    override suspend fun loadScannedDocuments(): Result<List<RemoteScannedDocument>> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        client.from("scanned_documents").select {
            filter { eq("user_id", user.id) }
            order("created_at", Order.DESCENDING)
        }.decodeList<ScannedDocumentRow>().map { it.toRemote() }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Load scanned documents failed", exception)
            Result.Error(scannerHistoryError("load", exception))
        }
    )

    override suspend fun upsertScannedDocument(
        document: RemoteScannedDocument,
        fileBytes: ByteArray?,
        thumbnailBytes: ByteArray?
    ): Result<Unit> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        val filePath = document.storageFilePath.ifBlank {
            scanStoragePath(user.id, document.id, document.mimeType)
        }
        val thumbPath = document.storageThumbnailPath ?: scanThumbnailPath(user.id, document.id)
        if (fileBytes != null && fileBytes.isNotEmpty()) {
            client.storage.from(ScanBucket).upload(filePath, fileBytes) {
                upsert = true
                contentType = contentTypeFor(document.mimeType)
            }
        }
        if (thumbnailBytes != null && thumbnailBytes.isNotEmpty()) {
            client.storage.from(ScanBucket).upload(thumbPath, thumbnailBytes) {
                upsert = true
                contentType = ContentType.Image.JPEG
            }
        }
        client.from("scanned_documents").upsert(
            document.toRow(
                userId = user.id,
                storageFilePath = filePath,
                storageThumbnailPath = if (
                    thumbnailBytes != null || document.storageThumbnailPath != null
                ) {
                    thumbPath
                } else {
                    null
                }
            )
        )
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Save scanned document failed", exception)
            Result.Error(scannerHistoryError("save", exception))
        }
    )

    override suspend fun downloadScanFile(storagePath: String): Result<ByteArray> = runCatching {
        require(storagePath.isNotBlank()) { "Missing scan file path" }
        client.auth.currentUserOrNull() ?: error("Not logged in")
        client.storage.from(ScanBucket).downloadAuthenticated(storagePath)
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Download scan file failed", exception)
            Result.Error(scannerHistoryError("download", exception))
        }
    )

    override suspend fun deleteScannedDocument(id: String): Result<Unit> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Not logged in")
        client.from("scanned_documents").delete {
            filter {
                eq("id", id)
                eq("user_id", user.id)
            }
        }
        val prefix = "${user.id}/$id"
        runCatching {
            client.storage.from(ScanBucket).delete(
                listOf(
                    "$prefix/document.pdf",
                    "$prefix/document.jpg",
                    "$prefix/thumbnail.jpg"
                )
            )
        }
        Unit
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { exception ->
            Log.e("SupabaseRepository", "Delete scanned document failed", exception)
            Result.Error(scannerHistoryError("delete", exception))
        }
    )

    private fun scannerHistoryError(action: String, exception: Throwable): String {
        val detail = exception.message.orEmpty()
        return when {
            detail.contains("Not logged in", ignoreCase = true) ->
                "Sign in to save scanner history on other phones."
            detail.contains("relation", ignoreCase = true) &&
                detail.contains("scanned_documents", ignoreCase = true) ->
                "Scanner history is not ready. Run supabase/scanned_documents_setup.sql."
            detail.contains("bucket", ignoreCase = true) &&
                detail.contains("not found", ignoreCase = true) ->
                "Scanner file storage is not ready. Run supabase/scanned_documents_setup.sql."
            detail.contains("row-level security", ignoreCase = true) ||
                detail.contains("permission denied", ignoreCase = true) ||
                detail.contains("42501") ->
                "Permission denied. Run supabase/scanned_documents_setup.sql."
            else -> "Unable to $action scanner history."
        }
    }

    private fun chatHistoryError(action: String, exception: Throwable): String {
        val detail = exception.message.orEmpty()
        return when {
            detail.contains("Not logged in", ignoreCase = true) ->
                "Sign in to save and view chat history."
            detail.contains("relation", ignoreCase = true) &&
                (detail.contains("chat_messages", ignoreCase = true) ||
                    detail.contains("chat_conversations", ignoreCase = true)) ->
                "Chat history is not ready. Run supabase/chat_messages_setup.sql."
            detail.contains("conversation_id", ignoreCase = true) ->
                "Chat history is not ready. Run supabase/chat_messages_setup.sql."
            detail.contains("row-level security", ignoreCase = true) ||
                detail.contains("permission denied", ignoreCase = true) ||
                detail.contains("42501") ->
                "Permission denied. Run supabase/chat_messages_setup.sql."
            else -> "Unable to $action chat history."
        }
    }

    private fun profileImageError(action: String, exception: Throwable): String {
        val detail = exception.message.orEmpty()
        return when {
            detail.contains("row-level security", ignoreCase = true) ||
                detail.contains("permission denied", ignoreCase = true) ||
                detail.contains("42501") -> {
                if (action == "upload") {
                    "Permission denied uploading to Storage. Run supabase/avatar_setup.sql in the Supabase SQL Editor."
                } else {
                    "Permission denied saving to profiles. Run supabase/avatar_setup.sql in the Supabase SQL Editor."
                }
            }
            detail.contains("bucket", ignoreCase = true) && detail.contains("not found", ignoreCase = true) ->
                "The Supabase Storage bucket named \"avatars\" does not exist. Run supabase/avatar_setup.sql."
            else -> "Unable to $action profile image: ${detail.ifBlank { "unknown Supabase error" }}"
        }
    }

}

@Serializable
private data class ProfileImage(
    @SerialName("profile_image_url") val url: String? = null
)

@Serializable
private data class ProfileImageUpsert(
    val id: String,
    val username: String,
    val email: String,
    @SerialName("profile_image_url") val profileImageUrl: String
)

@Serializable
private data class ProfileRow(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val age: Int? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val gender: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null
)

@Serializable
private data class ProfileUpsert(
    val id: String,
    val username: String,
    val email: String,
    val age: Int? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val gender: String? = null
)

@Serializable
private data class FeedbackInsert(
    @SerialName("user_id") val userId: String,
    val rating: Int,
    val category: String,
    val message: String,
    @SerialName("contact_email") val contactEmail: String? = null
)

@Serializable
private data class ChatMessageRow(
    val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("conversation_id") val conversationId: String? = null,
    val text: String,
    @SerialName("is_from_user") val isFromUser: Boolean,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
private data class ChatMessageInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("conversation_id") val conversationId: String,
    val text: String,
    @SerialName("is_from_user") val isFromUser: Boolean
)

@Serializable
private data class ConversationRow(
    val id: String,
    @SerialName("user_id") val userId: String? = null,
    val title: String = "New chat",
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
private data class ConversationInsert(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String
)

@Serializable
private data class ConversationTouch(
    @SerialName("updated_at") val updatedAt: String
)

private const val ScanBucket = "scans"

@Serializable
private data class ScannedDocumentRow(
    val id: String,
    @SerialName("user_id") val userId: String? = null,
    val name: String,
    val type: String,
    val source: String,
    @SerialName("mime_type") val mimeType: String = "image/jpeg",
    @SerialName("extracted_text") val extractedText: String = "",
    @SerialName("file_size_bytes") val fileSizeBytes: Long = 0,
    @SerialName("page_count") val pageCount: Int = 1,
    @SerialName("created_at") val createdAt: Long = 0,
    @SerialName("storage_file_path") val storageFilePath: String = "",
    @SerialName("storage_thumbnail_path") val storageThumbnailPath: String? = null,
    @SerialName("is_valid") val isValid: Boolean? = null,
    @SerialName("validation_status") val validationStatus: String = "NONE",
    @SerialName("document_kind") val documentKind: String = "",
    @SerialName("validation_summary") val validationSummary: String = "",
    @SerialName("validation_issues") val validationIssues: String = "",
    @SerialName("is_legal") val isLegal: Boolean? = null,
    @SerialName("legal_status") val legalStatus: String = "NONE",
    @SerialName("legal_statute") val legalStatute: String = "",
    @SerialName("legal_summary") val legalSummary: String = "",
    @SerialName("legal_violations") val legalViolations: String = "",
    @SerialName("legal_missing") val legalMissing: String = "",
    @SerialName("legal_next_steps") val legalNextSteps: String = ""
)

private fun ScannedDocumentRow.toRemote() = RemoteScannedDocument(
    id = id,
    name = name,
    type = type,
    source = source,
    mimeType = mimeType,
    extractedText = extractedText,
    fileSizeBytes = fileSizeBytes,
    pageCount = pageCount,
    createdAt = createdAt,
    storageFilePath = storageFilePath,
    storageThumbnailPath = storageThumbnailPath,
    isValid = isValid,
    validationStatus = validationStatus,
    documentKind = documentKind,
    validationSummary = validationSummary,
    validationIssues = validationIssues,
    isLegal = isLegal,
    legalStatus = legalStatus,
    legalStatute = legalStatute,
    legalSummary = legalSummary,
    legalViolations = legalViolations,
    legalMissing = legalMissing,
    legalNextSteps = legalNextSteps
)

private fun RemoteScannedDocument.toRow(
    userId: String,
    storageFilePath: String,
    storageThumbnailPath: String?
) = ScannedDocumentRow(
    id = id,
    userId = userId,
    name = name,
    type = type,
    source = source,
    mimeType = mimeType,
    extractedText = extractedText,
    fileSizeBytes = fileSizeBytes,
    pageCount = pageCount,
    createdAt = createdAt,
    storageFilePath = storageFilePath,
    storageThumbnailPath = storageThumbnailPath,
    isValid = isValid,
    validationStatus = validationStatus,
    documentKind = documentKind,
    validationSummary = validationSummary,
    validationIssues = validationIssues,
    isLegal = isLegal,
    legalStatus = legalStatus,
    legalStatute = legalStatute,
    legalSummary = legalSummary,
    legalViolations = legalViolations,
    legalMissing = legalMissing,
    legalNextSteps = legalNextSteps
)

private fun scanStoragePath(userId: String, documentId: String, mimeType: String): String {
    val fileName = if (mimeType.contains("pdf", ignoreCase = true)) {
        "document.pdf"
    } else {
        "document.jpg"
    }
    return "$userId/$documentId/$fileName"
}

private fun scanThumbnailPath(userId: String, documentId: String): String =
    "$userId/$documentId/thumbnail.jpg"

private fun contentTypeFor(mimeType: String): ContentType = when {
    mimeType.contains("pdf", ignoreCase = true) -> ContentType.Application.Pdf
    mimeType.contains("png", ignoreCase = true) -> ContentType.Image.PNG
    else -> ContentType.Image.JPEG
}
