package com.example.assignment.ui.verifyCode

data class VerifyCodeUiState(
    val email: String = "",
    val mode: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val message: String? = null,
    val error: String? = null,
    val navigateBack: Boolean = false,
    val navigateToResetPassword: Boolean = false
)
