package com.example.assignment.ui.ChatBox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.ChatBox.ChatConversation
import com.example.assignment.database.remote.ChatBox.ChatRepository
import com.example.assignment.database.remote.ChatBox.LegalChatAnswer
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
import java.util.UUID

class AiChatBoxViewModel(
    private val chatRepository: ChatRepository = ChatRepository(),
    private val historyRepository: Repository = SupabaseRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiChatBoxUiState())
    val uiState: StateFlow<AiChatBoxUiState> = _uiState.asStateFlow()
    private var hasLoadedConversations = false

    fun onEvent(event: AiChatBoxEvent) {
        when (event) {
            AiChatBoxEvent.ScreenOpened -> onScreenOpened()
            is AiChatBoxEvent.InputChanged -> _uiState.update { it.copy(inputText = event.value) }
            AiChatBoxEvent.SendClicked -> sendMessage()
            AiChatBoxEvent.NewChatClicked -> startNewChat()
            AiChatBoxEvent.HistoryClicked -> _uiState.update { it.copy(isHistoryOpen = true) }
            AiChatBoxEvent.HistoryDismissed -> _uiState.update { it.copy(isHistoryOpen = false) }
            is AiChatBoxEvent.ConversationSelected -> openConversation(event.conversationId)
            is AiChatBoxEvent.TabSelected -> selectTab(event.tab)
            AiChatBoxEvent.LogoutConfirmed -> confirmLogout()
            AiChatBoxEvent.LogoutCanceled -> _uiState.update { it.copy(showLogoutDialog = false) }
            AiChatBoxEvent.NavigationHandled -> _uiState.update { it.copy(navigateTo = null) }
            AiChatBoxEvent.MessageShown -> _uiState.update { it.copy(message = null) }
        }
    }

    private fun onScreenOpened() {
        _uiState.update {
            it.copy(selectedTab = ProfileTab.ChatBox, navigateTo = null)
        }
        if (!hasLoadedConversations) {
            loadConversations()
        }
        applyPendingChatLaunch()
    }

    private fun applyPendingChatLaunch() {
        val (action, conversationId) = PendingScreenAction.consumeChatLaunch()
        when (action) {
            PendingScreenAction.NEW_CHAT -> startNewChat()
            PendingScreenAction.CHAT_HISTORY,
            PendingScreenAction.HISTORY -> _uiState.update { it.copy(isHistoryOpen = true) }
        }
        if (!conversationId.isNullOrBlank()) {
            openConversation(conversationId)
        }
    }

    private fun loadConversations() {
        if (!historyRepository.isLoggedIn()) {
            hasLoadedConversations = true
            _uiState.update { it.copy(isLoadingHistory = false) }
            return
        }

        hasLoadedConversations = true
        viewModelScope.launch {
            when (val result = historyRepository.loadChatConversations()) {
                is Result.Success -> {
                    val currentId = _uiState.value.currentConversationId
                    val title = result.data.find { it.id == currentId }?.title
                    _uiState.update {
                        it.copy(
                            conversations = result.data,
                            isLoadingHistory = false,
                            conversationTitle = title ?: it.conversationTitle
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoadingHistory = false, message = result.message)
                }
            }
        }
    }

    private fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            when (val result = historyRepository.loadChatHistory(conversationId)) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        messages = result.data.map { row ->
                            ChatMessage(
                                id = row.id,
                                text = row.text,
                                isFromUser = row.isFromUser,
                                structured = if (row.isFromUser) {
                                    null
                                } else {
                                    LegalChatAnswer.parse(row.text)
                                }
                            )
                        },
                        isLoadingHistory = false
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoadingHistory = false, message = result.message)
                }
            }
        }
    }

    private fun startNewChat() {
        _uiState.update {
            it.copy(
                messages = emptyList(),
                currentConversationId = null,
                conversationTitle = "New chat",
                inputText = "",
                isSending = false,
                isHistoryOpen = false,
                isLoadingHistory = false
            )
        }
    }

    private fun openConversation(conversationId: String) {
        val conversation = _uiState.value.conversations.find { it.id == conversationId }
        _uiState.update {
            it.copy(
                currentConversationId = conversationId,
                conversationTitle = conversation?.title ?: it.conversationTitle.ifBlank { "Chat" },
                isHistoryOpen = false,
                isLoadingHistory = true,
                messages = emptyList()
            )
        }
        loadMessages(conversationId)
    }

    private fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isSending) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isFromUser = true
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isSending = true
            )
        }

        viewModelScope.launch {
            val conversationId = ensureConversation(text)
            if (conversationId != null) {
                persistMessage(conversationId, text, isFromUser = true)
            }

            when (val result = chatRepository.sendMessage(text)) {
                is Result.Success -> {
                    val reply = result.data
                    _uiState.update {
                        it.copy(
                            messages = it.messages + ChatMessage(
                                id = UUID.randomUUID().toString(),
                                text = reply.displayText,
                                isFromUser = false,
                                structured = reply
                            ),
                            isSending = false
                        )
                    }
                    if (conversationId != null) {
                        persistMessage(conversationId, reply.displayText, isFromUser = false)
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(
                        isSending = false,
                        message = result.message
                    )
                }
            }
        }
    }

    private suspend fun ensureConversation(firstMessage: String): String? {
        val existingId = _uiState.value.currentConversationId
        if (existingId != null) return existingId
        if (!historyRepository.isLoggedIn()) return null

        return when (val result = historyRepository.createChatConversation(firstMessage)) {
            is Result.Success -> {
                val conversation = result.data
                _uiState.update {
                    it.copy(
                        currentConversationId = conversation.id,
                        conversationTitle = conversation.title,
                        conversations = listOf(conversation) + it.conversations
                    )
                }
                conversation.id
            }
            is Result.Error -> {
                _uiState.update { it.copy(message = result.message) }
                null
            }
        }
    }

    private suspend fun persistMessage(
        conversationId: String,
        text: String,
        isFromUser: Boolean
    ) {
        if (!historyRepository.isLoggedIn()) return
        when (val result = historyRepository.saveChatMessage(conversationId, text, isFromUser)) {
            is Result.Error -> _uiState.update { it.copy(message = result.message) }
            is Result.Success -> {
                val current = _uiState.value.conversations.find { it.id == conversationId }
                    ?: ChatConversation(conversationId, _uiState.value.conversationTitle)
                _uiState.update {
                    it.copy(conversations = listOf(current) + it.conversations.filter { conv -> conv.id != conversationId })
                }
            }
        }
    }

    private fun selectTab(tab: ProfileTab) {
        if (tab == ProfileTab.ChatBox) return
        if (tab == ProfileTab.Logout) {
            _uiState.update { it.copy(showLogoutDialog = true) }
            return
        }

        val route = when (tab) {
            ProfileTab.Home -> ScreenRoutes.Home.route
            ProfileTab.Scanner -> ScreenRoutes.Scanner.route
            ProfileTab.Profile -> ScreenRoutes.Profile.route
            else -> null
        }

        _uiState.update {
            it.copy(
                selectedTab = tab,
                navigateTo = route
            )
        }
    }

    private fun confirmLogout() {
        _uiState.update { it.copy(showLogoutDialog = false) }
        viewModelScope.launch {
            historyRepository.logout()
            _uiState.update { it.copy(navigateTo = ScreenRoutes.Login.route) }
        }
    }

    override fun onCleared() {
        chatRepository.close()
        super.onCleared()
    }
}
