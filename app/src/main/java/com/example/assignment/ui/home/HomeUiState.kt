package com.example.assignment.ui.home

import com.example.assignment.ui.profile.ProfileTab

data class HomeRecentChat(
    val id: String,
    val title: String,
    val preview: String,
    val timeLabel: String
)

data class HomeUiState(
    val username: String = "",
    val profileImageUrl: String? = null,
    val recentChats: List<HomeRecentChat> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: ProfileTab = ProfileTab.Home,
    val showLogoutDialog: Boolean = false,
    val navigateTo: String? = null,
    val message: String? = null
)
