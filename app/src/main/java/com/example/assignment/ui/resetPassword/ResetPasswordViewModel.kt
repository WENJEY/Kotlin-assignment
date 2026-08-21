package com.example.assignment.ui.resetPassword

import io.github.jan.supabase.auth.auth
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.supabase.SupabaseClientProvider
import com.example.assignment.navigation.PasswordResetMode
import com.example.assignment.ui.utils.RegisterValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auth =
        SupabaseClientProvider.client.auth
    private val isChangePassword =
        savedStateHandle.get<String>("mode") == PasswordResetMode.Change

    private val _uiState =
        MutableStateFlow(
            ResetPasswordUiState(isChangePassword = isChangePassword)
        )

    val uiState: StateFlow<ResetPasswordUiState> =
        _uiState.asStateFlow()


    fun onEvent(event: ResetPasswordEvent) {

        when (event) {
            is ResetPasswordEvent.PasswordChanged -> {

                _uiState.update {

                    it.copy(
                        password = event.value,
                        error = null,
                        message = null
                    )
                }
            }

            is ResetPasswordEvent.ConfirmPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        confirmPassword = event.value,
                        error = null,
                        message = null
                    )
                }
            }

            ResetPasswordEvent.TogglePasswordVisibility -> {
                _uiState.update {
                    it.copy(
                        passwordVisible =
                            !it.passwordVisible
                    )
                }
            }

            ResetPasswordEvent.ToggleConfirmPasswordVisibility -> {
                _uiState.update {
                    it.copy(
                        confirmPasswordVisible =
                            !it.confirmPasswordVisible
                    )
                }
            }

            ResetPasswordEvent.UpdatePasswordClicked -> {
                updatePassword()
            }

            ResetPasswordEvent.ResetSuccessClicked -> {
                _uiState.update {
                    it.copy(
                        showSuccessDialog = false,
                        navigateToLogin = !isChangePassword,
                        navigateToProfile = isChangePassword
                    )
                }
            }

            ResetPasswordEvent.BackToLoginClicked -> {
                _uiState.update {
                    it.copy(
                        navigateToLogin = !isChangePassword,
                        navigateToProfile = isChangePassword
                    )
                }
            }

            ResetPasswordEvent.NavigationHandled -> {
                _uiState.update {
                    it.copy(
                        navigateToLogin = false,
                        navigateToProfile = false
                    )
                }
            }
        }
    }

    private fun updatePassword() {

        val password = _uiState.value.password.trim()
        val confirmPassword = _uiState.value.confirmPassword.trim()

        RegisterValidator.validatePassword(password)?.let { error ->
            _uiState.update {
                it.copy(
                    error = error,
                    message = null
                )
            }
            return
        }

        RegisterValidator.validateConfirmPassword(
            password,
            confirmPassword
        )?.let { error ->
            _uiState.update {
                it.copy(
                    error = error,
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
                auth.updateUser {
                    this.password = password
                }

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        error = null,
                        message = "Password updated successfully.",
                        showSuccessDialog = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to update password. " + "Please try again.",
                        message = null
                    )
                }
            }
        }
    }
}