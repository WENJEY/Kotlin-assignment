package com.example.assignment.ui.forgotPassword

sealed class ForgotPasswordEvent {
    data class EmailChanged(val value : String) : ForgotPasswordEvent()

    data object BackToLoginClicked : ForgotPasswordEvent();

    data object SendVerificationCodeClicked : ForgotPasswordEvent();

    data object NavigationHandled : ForgotPasswordEvent();

    data object ScreenResumed : ForgotPasswordEvent();
}
