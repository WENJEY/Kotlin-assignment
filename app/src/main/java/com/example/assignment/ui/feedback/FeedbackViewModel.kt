package com.example.assignment.ui.feedback

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Feedback
import com.example.assignment.database.Repository
import com.example.assignment.database.SupabaseRepository
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val repository: Repository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FeedbackUiState(contactEmail = repository.currentUser()?.email.orEmpty())
    )
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun onEvent(event: FeedbackEvent) {
        when (event) {
            is FeedbackEvent.RatingSelected -> _uiState.update {
                it.copy(rating = event.rating, ratingError = null)
            }
            is FeedbackEvent.CategorySelected -> _uiState.update {
                it.copy(
                    category = event.category,
                    categoryError = null,
                    isCategoryMenuOpen = false
                )
            }
            is FeedbackEvent.MessageChanged -> _uiState.update {
                it.copy(
                    message = event.value.take(500),
                    messageError = null
                )
            }
            is FeedbackEvent.ContactEmailChanged -> _uiState.update {
                it.copy(contactEmail = event.value, emailError = null)
            }
            FeedbackEvent.CategoryClicked -> _uiState.update {
                it.copy(isCategoryMenuOpen = true)
            }
            FeedbackEvent.CategoryDismissed -> _uiState.update {
                it.copy(isCategoryMenuOpen = false)
            }
            FeedbackEvent.SubmitClicked -> submitFeedback()
            FeedbackEvent.BackClicked -> _uiState.update { it.copy(navigateBack = true) }
            FeedbackEvent.NavigationHandled -> _uiState.update { it.copy(navigateBack = false) }
            FeedbackEvent.MessageShown -> _uiState.update {
                it.copy(successMessage = null, errorMessage = null)
            }
        }
    }

    private fun submitFeedback() {
        if (_uiState.value.isSubmitting) return

        val state = _uiState.value
        val message = state.message.trim()
        val email = state.contactEmail.trim()
        val ratingError = if (state.rating == null) "Please select your experience" else null
        val categoryError = if (state.category.isBlank()) "Please select a category" else null
        val messageError = if (message.isBlank()) "Please enter your feedback" else null
        val emailError = if (
            email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            "Please enter a valid email address"
        } else {
            null
        }

        if (ratingError != null || categoryError != null ||
            messageError != null || emailError != null
        ) {
            _uiState.update {
                it.copy(
                    ratingError = ratingError,
                    categoryError = categoryError,
                    messageError = messageError,
                    emailError = emailError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, errorMessage = null, successMessage = null)
            }
            val feedback = Feedback(
                rating = state.rating!!,
                category = state.category,
                message = message,
                contactEmail = email.ifBlank { null }
            )
            when (val result = repository.submitFeedback(feedback)) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        rating = null,
                        category = "",
                        message = "",
                        isSubmitting = false,
                        successMessage = "Thank you! Your feedback was sent."
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = result.message)
                }
            }
        }
    }
}
