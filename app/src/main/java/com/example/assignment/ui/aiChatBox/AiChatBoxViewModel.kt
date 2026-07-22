package com.example.assignment.ui.aiChatBox

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AiChatBoxViewModel : ViewModel() {
    private val  _uiState= MutableStateFlow(AiChatBoxUiState())
    val uiState : StateFlow<AiChatBoxUiState> = _uiState.asStateFlow()
}