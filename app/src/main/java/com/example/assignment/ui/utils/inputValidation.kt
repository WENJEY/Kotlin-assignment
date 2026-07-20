package com.example.assignment.ui.utils

object InputValidator {

    // Username Validation
    internal fun validateUsername(username: String): String? = when {
        username.isBlank() -> "Username is required"
        username.length < 3 -> "Username must be at least 3 characters"
        username.length > 20 -> "Username must be less than 20 characters"
        else -> null
    }

    // Email Validation
    internal fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        else -> null
    }

    // Password Validation
    internal fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Password must contain uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain lowercase letter"
        !password.any { it.isDigit() } -> "Password must contain number"
        !password.any { it in "!@#$%^&*()_+-=[]{}|;':\",./<>?" } ->
            "Password must contain special character"
        else -> null
    }
}

// Confirm Password Validation
internal fun validateConfirmPassword(password: String, confirmPassword: String): String? = when {
    confirmPassword.isBlank() -> "Please confirm your password"
    confirmPassword != password -> "Passwords do not match"

    else -> null
}
