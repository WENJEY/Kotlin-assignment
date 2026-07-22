package com.example.assignment.ui.login

import com.example.assignment.ui.navigation.ScreenRoutes

data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val navigateTo: ScreenRoutes? = null
)