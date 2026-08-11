package com.example.assignment.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.Repository
import com.example.assignment.database.SupabaseRepository
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.utils.RegisterValidator.validateConfirmPassword
import com.example.assignment.ui.utils.RegisterValidator.validateEmail
import com.example.assignment.ui.utils.RegisterValidator.validatePassword
import com.example.assignment.ui.utils.RegisterValidator.validateUsername
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: Repository = SupabaseRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged -> {
                _uiState.update {
                    it.copy(
                        username = event.value,
                        usernameError = null,
                        error = null
                    )
                }
            }

            is RegisterEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.value,
                        emailError = null,
                        error = null
                    )
                }
            }

            is RegisterEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.value,
                        passwordError = null,
                        error = null
                    )
                }
            }

            is RegisterEvent.ConfirmPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        confirmPassword = event.value,
                        confirmPasswordError = null,
                        error = null
                    )
                }
            }

            is RegisterEvent.SignUpSuccessClicked -> {
                _uiState.update {
                    it.copy(
                        showSuccessDialog = false,
                        navigateTo = ScreenRoutes.Login
                    )
                }
            }

                RegisterEvent.SignUpClicked -> signUp()

                RegisterEvent.LoginClicked -> {
                    _uiState.update {
                        it.copy(navigateTo = ScreenRoutes.Login)
                    }
                }

                RegisterEvent.NavigationHandled -> {
                    _uiState.update {
                        it.copy(navigateTo = null)
                    }
                }
            }
        }

        private fun signUp() {
            val state = _uiState.value

            if (state.isLoading) return

            val usernameError = validateUsername(state.username)
            val emailError = validateEmail(state.email)
            val passwordError = validatePassword(state.password)
            val confirmPasswordError = validateConfirmPassword(
                state.password,
                state.confirmPassword
            )

            if (usernameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = usernameError,
                        emailError = emailError,
                        passwordError = passwordError,
                        confirmPasswordError = confirmPasswordError
                    )
                }
                return
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            viewModelScope.launch {
                when (
                    val result = repository.signUp(
                        username = state.username,
                        email = state.email,
                        password = state.password
                    )
                ) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistered = true,
                                showSuccessDialog = true
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
