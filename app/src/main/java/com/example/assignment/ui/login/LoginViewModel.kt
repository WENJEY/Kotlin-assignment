package com.example.assignment.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.ui.navigation.ScreenRoutes
import com.example.assignment.ui.utils.LoginValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val validator: LoginValidator = LoginValidator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.IdentifierChanged -> {
                _uiState.update {
                    it.copy(
                        identifier = event.value,
                        error = null
                    )
                }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.value,
                        error = null
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

        if (state.identifier.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Invalid username/email or password"
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
            val loginError = validator.validateLogin(
                state.identifier,
                state.password
            )

            if (loginError != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = loginError
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigateTo = ScreenRoutes.Home
                    )
                }
            }
        }
    }
}