package com.example.assignment.ui.profile
data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val selectedBottomTab: ProfileBottomTab = ProfileBottomTab.Profile,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateTo: String? = null
)

enum class ProfileBottomTab {
    Home, Scanner , ChatBox, Profile
}

