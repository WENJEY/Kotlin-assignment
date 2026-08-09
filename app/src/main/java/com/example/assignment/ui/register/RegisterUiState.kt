package com.example.assignment.ui.register

import com.example.assignment.navigation.ScreenRoutes

data class RegisterUiState (
    val username: String = "",
    val email : String = "",
    val password: String = "",
    val confirmPassword : String = "",
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val showSuccessDialog : Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateTo: ScreenRoutes? = null,
    val isRegistered: Boolean = false
)
