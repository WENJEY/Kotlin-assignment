package com.example.assignment.ui.forgotPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val auth =
        SupabaseClientProvider.client.auth

    private val _uiState =
        MutableStateFlow(ForgotPasswordUiState())

    val uiState: StateFlow<ForgotPasswordUiState> =
        _uiState.asStateFlow()

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()
    }

    fun onEvent(event: ForgotPasswordEvent) {

        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.value,
                        error = null,
                        message = null
                    )
                }
            }

            ForgotPasswordEvent.SendResetLinkClicked -> {
                sendResetEmail()
            }

            ForgotPasswordEvent.BackToLoginClicked -> {
                _uiState.update {
                    it.copy(
                        navigateBack = true
                    )
                }
            }

            ForgotPasswordEvent.NavigationHandled -> {
                _uiState.update {
                    it.copy(
                        navigateBack = false
                    )
                }
            }
        }
    }

    private fun sendResetEmail() {

        val email =
            _uiState.value.email.trim()

        if (email.isEmpty()) {
            _uiState.update {
                it.copy(
                    error = "Please enter your email.",
                    message = null
                )
            }
            return
        }

        if (!isValidEmail(email)) {
            _uiState.update {
                it.copy(
                    error = "Please enter a valid email address.",
                    message = null
                )
            }
            return
        }

        _uiState.update {

            it.copy(
                isLoading = true,
                error = null,
                message = null
            )
        }

        viewModelScope.launch {
            try {
                auth.resetPasswordForEmail(
                    email = email,
                    redirectUrl = "com.example.assignment://reset-password"
                )
                // Success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        message = "Password reset email sent."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to send password reset email. " + "Please try again.",
                        message = null
                    )
                }
            }
        }
    }
}