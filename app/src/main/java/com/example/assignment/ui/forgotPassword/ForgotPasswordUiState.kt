package com.example.assignment.ui.forgotPassword


data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val navigateBack: Boolean = false
)


