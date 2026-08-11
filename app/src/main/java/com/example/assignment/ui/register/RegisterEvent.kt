package com.example.assignment.ui.register


sealed class RegisterEvent {
    data class UsernameChanged (val value : String ) : RegisterEvent()

    data class EmailChanged (val value : String) : RegisterEvent()

    data class PasswordChanged (val value : String) : RegisterEvent()

    data class ConfirmPasswordChanged (val value : String) : RegisterEvent()

    data object SignUpClicked : RegisterEvent()
    data object SignUpSuccessClicked : RegisterEvent()
    data object LoginClicked : RegisterEvent()
    data object NavigationHandled : RegisterEvent()

}
