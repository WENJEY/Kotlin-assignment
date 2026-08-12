package com.example.assignment.database

import android.util.Log
import com.example.assignment.ui.utils.Result
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
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

    override suspend fun resetPassword(
        email: String
    ): Result<Unit> {

        return try {

            auth.resetPasswordForEmail(
                email = email.trim()
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
    private fun mapResetPasswordError(
        exception: Exception
    ): String {

        val message = exception.message.orEmpty()

        Log.e(
            "SupabaseRepository",
            "Raw password reset error: $message"
        )

        return when {

            message.contains("rate limit", ignoreCase = true) -> {
                "Too many requests. Please try again later."
            }

            message.contains("invalid email", ignoreCase = true) ||
                    message.contains("valid email", ignoreCase = true) -> {
                "Please enter a valid email address."
            }
            else ->
                { "Unable to send password reset email. Please try again."
            }
        }
    }

}