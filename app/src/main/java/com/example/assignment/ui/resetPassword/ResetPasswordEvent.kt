package com.example.assignment.ui.resetPassword

import com.example.assignment.ui.register.RegisterEvent

sealed interface ResetPasswordEvent {

    data class PasswordChanged(val value: String) : ResetPasswordEvent

    data class ConfirmPasswordChanged(val value: String) : ResetPasswordEvent

    data object TogglePasswordVisibility : ResetPasswordEvent

    data object ToggleConfirmPasswordVisibility : ResetPasswordEvent

    data object UpdatePasswordClicked : ResetPasswordEvent

    data object ResetSuccessClicked : ResetPasswordEvent

    data object BackToLoginClicked : ResetPasswordEvent

    data object NavigationHandled : ResetPasswordEvent
}