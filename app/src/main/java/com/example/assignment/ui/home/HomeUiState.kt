package com.example.assignment.ui.home

import com.example.assignment.navigation.ScreenRoutes

data class HomeUiState (
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val navigateTo: ScreenRoutes? = null
)