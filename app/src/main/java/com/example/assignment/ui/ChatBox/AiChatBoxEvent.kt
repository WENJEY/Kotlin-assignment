package com.example.assignment.ui.ChatBox

import com.example.assignment.ui.profile.ProfileTab

sealed class AiChatBoxEvent {
    data object ScreenOpened : AiChatBoxEvent()
    data class InputChanged(val value: String) : AiChatBoxEvent()
    data object SendClicked : AiChatBoxEvent()
    data object NewChatClicked : AiChatBoxEvent()
    data object HistoryClicked : AiChatBoxEvent()
    data object HistoryDismissed : AiChatBoxEvent()
    data class ConversationSelected(val conversationId: String) : AiChatBoxEvent()
    data class TabSelected(val tab: ProfileTab) : AiChatBoxEvent()
    data object LogoutConfirmed : AiChatBoxEvent()
    data object LogoutCanceled : AiChatBoxEvent()
    data object NavigationHandled : AiChatBoxEvent()
    data object MessageShown : AiChatBoxEvent()
}
