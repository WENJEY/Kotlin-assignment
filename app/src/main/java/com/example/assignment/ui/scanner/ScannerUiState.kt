package com.example.assignment.ui.scanner

import com.example.assignment.ui.profile.ProfileTab

enum class DocumentType {
    PDF,
    IMAGE
}

data class RecentDocument(
    val id: String,
    val name: String,
    val metadata: String,
    val type: DocumentType
)

data class ScannerUiState(
    val greeting: String = "Good morning! 👋",
    val subtitle: String = "Let's scan something today.",
    val recentDocuments: List<RecentDocument> = emptyList(),
    val selectedTab: ProfileTab = ProfileTab.Scanner,
    val navigateTo: String? = null,
    val message: String? = null
)
