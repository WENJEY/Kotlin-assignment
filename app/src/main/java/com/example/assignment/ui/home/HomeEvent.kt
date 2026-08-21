package com.example.assignment.ui.home

import com.example.assignment.ui.profile.ProfileTab

sealed class HomeEvent {
    data object ScreenOpened : HomeEvent()
    data object ScanDocumentClicked : HomeEvent()
    data object ImportPdfClicked : HomeEvent()
    data object NewChatClicked : HomeEvent()
    data object HistoryClicked : HomeEvent()
    data object ViewAllChatsClicked : HomeEvent()
    data class ConversationClicked(val conversationId: String) : HomeEvent()
    data object ProfileClicked : HomeEvent()
    data class TabSelected(val tab: ProfileTab) : HomeEvent()
    data object NavigationHandled : HomeEvent()
    data object MessageShown : HomeEvent()
}
