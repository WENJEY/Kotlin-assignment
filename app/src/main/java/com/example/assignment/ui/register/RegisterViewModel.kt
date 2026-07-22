package com.example.assignment.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.ui.database.FirebaseRepository
import com.example.assignment.ui.database.Repository
import com.example.assignment.ui.utils.Result
import com.example.assignment.ui.navigation.ScreenRoutes
import com.example.assignment.ui.utils.RegisterValidator
import com.example.assignment.ui.utils.RegisterValidator.validateConfirmPassword
import com.example.assignment.ui.utils.RegisterValidator.validateEmail
import com.example.assignment.ui.utils.RegisterValidator.validatePassword
import com.example.assignment.ui.utils.RegisterValidator.validateUsername
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: Repository = FirebaseRepository(),
) : ViewModel(){
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged -> {
                _uiState.update {
                    it.copy(
                        username = event.value,
                        usernameError = null
                    )
                }
            }

            is RegisterEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.value,
                        emailError = null
                    )
                }
            }

            is RegisterEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.value,
                        passwordError = null
                    )
                }
            }

            is RegisterEvent.ConfirmPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        confirmPassword = event.value,
                        confirmPasswordError = null
                    )
                }
            }

            is RegisterEvent.SignUpClicked -> signUp()

            is RegisterEvent.LoginClicked -> {
                _uiState.update { it.copy(navigateTo = ScreenRoutes.Login) }
            }

            is RegisterEvent.NavigationHandled -> {
                _uiState.update { it.copy(navigateTo = null) }
            }
        }
    }

    private fun signUp() {
        val state = _uiState.value

        val usernameError = validateUsername(state.username)
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(state.password, state.confirmPassword)

        if (usernameError != null || emailError != null ||
            passwordError != null || confirmPasswordError != null
        ) {

            _uiState.update {
                it.copy(
                    usernameError = usernameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = repository.signUp(
                username = state.username,
                email = state.email,
                password = state.password
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            navigateTo = ScreenRoutes.Home
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