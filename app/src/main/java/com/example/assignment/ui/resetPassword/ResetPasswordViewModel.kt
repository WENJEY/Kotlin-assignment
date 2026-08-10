package com.example.assignment.ui.resetPassword

import io.github.jan.supabase.auth.auth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.SupabaseClientProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    private val auth =
        SupabaseClientProvider.client.auth

    private val _uiState =
        MutableStateFlow(
            ResetPasswordUiState()
        )

    val uiState: StateFlow<ResetPasswordUiState> =
        _uiState.asStateFlow()


    fun onEvent(event: ResetPasswordEvent) {

        when (event) {

            // =============================================
            // PASSWORD CHANGED
            // =============================================

            is ResetPasswordEvent.PasswordChanged -> {

                _uiState.update {

                    it.copy(
                        password = event.value,
                        error = null,
                        message = null
                    )
                }
            }


            // =============================================
            // CONFIRM PASSWORD CHANGED
            // =============================================

            is ResetPasswordEvent.ConfirmPasswordChanged -> {

                _uiState.update {

                    it.copy(
                        confirmPassword = event.value,
                        error = null,
                        message = null
                    )
                }
            }


            // =============================================
            // SHOW / HIDE PASSWORD
            // =============================================

            ResetPasswordEvent.TogglePasswordVisibility -> {

                _uiState.update {

                    it.copy(
                        passwordVisible =
                            !it.passwordVisible
                    )
                }
            }


            // =============================================
            // SHOW / HIDE CONFIRM PASSWORD
            // =============================================

            ResetPasswordEvent.ToggleConfirmPasswordVisibility -> {

                _uiState.update {

                    it.copy(
                        confirmPasswordVisible =
                            !it.confirmPasswordVisible
                    )
                }
            }


            // =============================================
            // UPDATE PASSWORD
            // =============================================

            ResetPasswordEvent.UpdatePasswordClicked -> {

                updatePassword()
            }


            // =============================================
            // BACK TO LOGIN
            // =============================================

            ResetPasswordEvent.BackToLoginClicked -> {

                _uiState.update {

                    it.copy(
                        navigateToLogin = true
                    )
                }
            }


            // =============================================
            // NAVIGATION HANDLED
            // =============================================

            ResetPasswordEvent.NavigationHandled -> {

                _uiState.update {

                    it.copy(
                        navigateToLogin = false
                    )
                }
            }
        }
    }


    // =====================================================
    // UPDATE PASSWORD
    // =====================================================

    private fun updatePassword() {

        val password =
            _uiState.value.password.trim()

        val confirmPassword =
            _uiState.value.confirmPassword.trim()


        // -------------------------------------------------
        // EMPTY PASSWORD
        // -------------------------------------------------

        if (password.isEmpty()) {

            _uiState.update {

                it.copy(
                    error =
                        "Please enter your new password.",
                    message = null
                )
            }

            return
        }


        // -------------------------------------------------
        // PASSWORD LENGTH
        // -------------------------------------------------

        if (password.length < 6) {

            _uiState.update {

                it.copy(
                    error =
                        "Password must be at least 6 characters.",
                    message = null
                )
            }

            return
        }


        // -------------------------------------------------
        // PASSWORDS MATCH
        // -------------------------------------------------

        if (password != confirmPassword) {

            _uiState.update {

                it.copy(
                    error =
                        "Passwords do not match.",
                    message = null
                )
            }

            return
        }


        // -------------------------------------------------
        // LOADING
        // -------------------------------------------------

        _uiState.update {

            it.copy(
                isLoading = true,
                error = null,
                message = null
            )
        }


        // -------------------------------------------------
        // SUPABASE UPDATE
        // -------------------------------------------------

        viewModelScope.launch {

            try {

                auth.updateUser {

                    this.password = password
                }


                // -----------------------------------------
                // SUCCESS
                // -----------------------------------------

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        error = null,
                        message =
                            "Password updated successfully."
                    )
                }

            } catch (e: Exception) {

                // -----------------------------------------
                // DON'T SHOW SUPABASE ERROR
                // -----------------------------------------

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        error =
                            "Unable to update password. " +
                                    "Please try again.",
                        message = null
                    )
                }
            }
        }
    }
}