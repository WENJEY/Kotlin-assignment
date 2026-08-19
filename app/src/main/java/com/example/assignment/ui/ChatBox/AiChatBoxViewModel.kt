package com.example.assignment.ui.ChatBox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.ChatConversation
import com.example.assignment.database.remote.ChatRepository
import com.example.assignment.database.remote.Repository
import com.example.assignment.database.remote.SupabaseRepository
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
                    _uiState.update {
                        it.copy(
                            conversations = result.data,
                            isLoadingHistory = false
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
                                isFromUser = row.isFromUser
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
        val conversation = _uiState.value.conversations.find { it.id == conversationId } ?: return
        _uiState.update {
            it.copy(
                currentConversationId = conversation.id,
                conversationTitle = conversation.title,
                isHistoryOpen = false,
                isLoadingHistory = true,
                messages = emptyList()
            )
        }
        loadMessages(conversation.id)
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
                                text = reply,
                                isFromUser = false
                            ),
                            isSending = false
                        )
                    }
                    if (conversationId != null) {
                        persistMessage(conversationId, reply, isFromUser = false)
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
        if (tab == ProfileTab.ChatBox || tab == ProfileTab.Logout) return

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

    override fun onCleared() {
        chatRepository.close()
        super.onCleared()
    }
}
