package com.example.assignment.ui.ChatBox

import com.example.assignment.database.remote.ChatBox.ChatConversation
import com.example.assignment.database.remote.ChatBox.LegalChatAnswer
import com.example.assignment.ui.profile.ProfileTab

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val structured: LegalChatAnswer? = null
)

data class AiChatBoxUiState(
    val messages: List<ChatMessage> = emptyList(),
    val conversations: List<ChatConversation> = emptyList(),
    val currentConversationId: String? = null,
    val conversationTitle: String = "New chat",
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val selectedTab: ProfileTab = ProfileTab.ChatBox,
    val showLogoutDialog: Boolean = false,
    val navigateTo: String? = null,
    val message: String? = null
)
