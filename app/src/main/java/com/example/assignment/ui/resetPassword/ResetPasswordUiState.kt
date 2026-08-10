package com.example.assignment.ui.resetPassword

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",

    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,

    val error: String? = null,
    val message: String? = null,

    val navigateToLogin: Boolean = false
)