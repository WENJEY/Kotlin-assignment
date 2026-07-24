package com.example.assignment.ui.forgetPassword

sealed class ForgotPasswordEvent {
    data class EmailChanged(val value : String) : ForgotPasswordEvent()

    data object BackToLoginClicked : ForgotPasswordEvent();

    data object SendResetLinkClicked : ForgotPasswordEvent();

    data object NavigationHandled : ForgotPasswordEvent();
}