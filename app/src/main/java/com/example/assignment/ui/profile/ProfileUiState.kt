package com.example.assignment.ui.profile
data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val selectedTab: ProfileTab = ProfileTab.Profile,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showLogoutDialog : Boolean = false,
    val navigateTo: String? = null
)

enum class ProfileTab {
    Home, Scanner , ChatBox, Profile,Logout
}

