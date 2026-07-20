package com.example.assignment.ui.login

import com.example.assignment.ui.navigation.ScreenRoutes

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateTo: ScreenRoutes? = null,
    val isLoggedIn: Boolean = false
)