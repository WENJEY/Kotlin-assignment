package com.example.assignment.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.ui.database.FirebaseRepository
import com.example.assignment.ui.database.Repository
import com.example.assignment.ui.navigation.ScreenRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.assignment.ui.utils.Result
import com.example.assignment.ui.utils.InputValidator.validateEmail
import com.example.assignment.ui.utils.InputValidator.validatePassword

class LoginViewModel(
    private val repository: Repository = FirebaseRepository()
) : ViewModel() {

    // Private mutable state - only ViewModel can modify
    private val _uiState = MutableStateFlow(LoginUiState())

    // Public immutable state - UI observes this
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // EVENT HANDLER (Single entry point)
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.value,
                        emailError = null      // clear error on new input
                    )
                }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.value,
                        passwordError = null
                    )
                }
            }
            is LoginEvent.LoginClicked -> login()

            is LoginEvent.SignUpClicked -> {
                _uiState.update { it.copy(navigateTo = ScreenRoutes.Register) }
            }
            is LoginEvent.NavigationHandled -> {
                _uiState.update { it.copy(navigateTo = null) }
            }
        }
    }

    private fun login() {
        val state = _uiState.value

        // Input validation
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        // Start loading
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = repository.login(state.email, state.password)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
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