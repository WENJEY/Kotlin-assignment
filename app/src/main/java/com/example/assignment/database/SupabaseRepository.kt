package com.example.assignment.database

import android.util.Log
import com.example.assignment.database.SupabaseClientProvider.client
import com.example.assignment.ui.utils.Result
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class SupabaseRepository : Repository {
    private val auth get() = SupabaseClientProvider.client.auth

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
        val email = SupabaseClientProvider.client.postgrest
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
