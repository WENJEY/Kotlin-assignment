package com.example.assignment.ui.utils

import com.example.assignment.database.remote.Repository
import com.example.assignment.database.remote.SupabaseRepository

object RegisterValidator {
     fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> "Invalid email format"
        else -> null
    }

     fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Password must contain uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain lowercase letter"
        !password.any { it.isDigit() } -> "Password must contain number"
        !password.any { it in "!@#$%^&*()_+-=[]{}|;':\",./<>?" } ->
            "Password must contain special character"
        else -> null
    }

    // Username Validation
    fun validateUsername(username: String): String? = when {
        username.isBlank() -> "Username is required"
        username.length < 3 -> "Username must be at least 3 characters"
        username.length > 20 -> "Username must be less than 20 characters"
        else -> null
    }

    // Confirm Password Validation
    fun validateConfirmPassword(password: String, confirmPassword: String): String? = when {
        confirmPassword.isBlank() -> "Please confirm your password"
        confirmPassword != password -> "Passwords do not match"
        else -> null
    }
}
class LoginValidator(
    private val repository: Repository = SupabaseRepository()
) {
    suspend fun validateLogin(identifier: String, password: String): String? {
        if (identifier.isBlank() || password.isBlank()) {
            return "Please enter email/username and password"
        }

        val trimmed = identifier.trim()

        val email = if (trimmed.contains("@")) {
            trimmed
        } else {
            when (val result = repository.getEmailByUsername(trimmed)) {
                is Result.Success -> result.data
                is Result.Error -> return result.message
            }
        }

        return when (val result = repository.login(email, password)) {
            is Result.Success -> null
            is Result.Error -> result.message
        }
    }
}