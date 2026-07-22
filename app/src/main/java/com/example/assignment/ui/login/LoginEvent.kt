package com.example.assignment.ui.login

sealed class LoginEvent {
    data class IdentifierChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    data object LoginClicked : LoginEvent()
    data object SignUpClicked : LoginEvent()
    data object NavigationHandled : LoginEvent()
}