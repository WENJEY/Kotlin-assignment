package com.example.assignment.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.Repository
import com.example.assignment.database.remote.supabase.SupabaseRepository
import com.example.assignment.navigation.PendingScreenAction
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.profile.ProfileTab
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

class HomeViewModel(
    private val repository: Repository = SupabaseRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.ScreenOpened -> loadHome()
            HomeEvent.ScanDocumentClicked -> openScanner(PendingScreenAction.SCAN)
            HomeEvent.ImportPdfClicked -> openScanner(PendingScreenAction.IMPORT)
            HomeEvent.NewChatClicked -> openChat(PendingScreenAction.NEW_CHAT)
            HomeEvent.HistoryClicked -> openScanner(PendingScreenAction.HISTORY)
            HomeEvent.ViewAllChatsClicked -> openChat(PendingScreenAction.CHAT_HISTORY)
            is HomeEvent.ConversationClicked -> openConversation(event.conversationId)
            HomeEvent.ProfileClicked -> navigateTo(ScreenRoutes.Profile.route)
            is HomeEvent.TabSelected -> selectTab(event.tab)
            HomeEvent.NavigationHandled -> _uiState.update { it.copy(navigateTo = null) }
            HomeEvent.MessageShown -> _uiState.update { it.copy(message = null) }
        }
    }

    private fun loadHome() {
        val user = repository.currentUser()
        _uiState.update {
            it.copy(
                username = user?.username.orEmpty(),
                selectedTab = ProfileTab.Home,
                isLoading = it.recentChats.isEmpty()
            )
        }

        viewModelScope.launch {
            val imageUrl = if (user == null) {
                null
            } else {
                when (val result = repository.getProfileImageUrl()) {
                    is Result.Success -> result.data ?: user.profileImageUrl
                    is Result.Error -> user.profileImageUrl
                }
            }

            val chats = if (repository.isLoggedIn()) {
                when (val result = repository.loadChatConversations()) {
                    is Result.Success -> result.data.take(4).map { conversation ->
                        HomeRecentChat(
                            id = conversation.id,
                            title = conversation.title.ifBlank { "New chat" },
                            preview = "Tap to continue this conversation",
                            timeLabel = relativeTimeLabel(conversation.updatedAt)
                        )
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(message = result.message) }
                        emptyList()
                    }
                }
            } else {
                emptyList()
            }

            _uiState.update {
                it.copy(
                    username = user?.username.orEmpty(),
                    profileImageUrl = imageUrl,
                    recentChats = chats,
                    isLoading = false
                )
            }
        }
    }

    private fun openScanner(action: String) {
        PendingScreenAction.scannerLaunch = action
        navigateTo(ScreenRoutes.Scanner.route)
    }

    private fun openChat(action: String) {
        PendingScreenAction.chatLaunch = action
        PendingScreenAction.chatConversationId = null
        navigateTo(ScreenRoutes.ChatBox.route)
    }

    private fun openConversation(conversationId: String) {
        PendingScreenAction.chatLaunch = null
        PendingScreenAction.chatConversationId = conversationId
        navigateTo(ScreenRoutes.ChatBox.route)
    }

    private fun selectTab(tab: ProfileTab) {
        if (tab == ProfileTab.Home || tab == ProfileTab.Logout) return

        val route = when (tab) {
            ProfileTab.Scanner -> ScreenRoutes.Scanner.route
            ProfileTab.ChatBox -> ScreenRoutes.ChatBox.route
            ProfileTab.Profile -> ScreenRoutes.Profile.route
            else -> null
        } ?: return

        _uiState.update {
            it.copy(
                selectedTab = tab,
                navigateTo = route
            )
        }
    }

    private fun navigateTo(route: String) {
        _uiState.update { it.copy(navigateTo = route) }
    }
}

private fun relativeTimeLabel(updatedAt: String?): String {
    if (updatedAt.isNullOrBlank()) return ""
    val instant = runCatching { Instant.parse(updatedAt) }.getOrNull()
        ?: runCatching { OffsetDateTime.parse(updatedAt).toInstant() }.getOrNull()
        ?: return ""
    val seconds = Duration.between(instant, Instant.now()).seconds.coerceAtLeast(0)
    return when {
        seconds < 60 -> "now"
        seconds < 3600 -> "${seconds / 60}m ago"
        seconds < 86400 -> "${seconds / 3600}h ago"
        seconds < 604800 -> "${seconds / 86400}d ago"
        else -> "${seconds / 604800}w ago"
    }
}
