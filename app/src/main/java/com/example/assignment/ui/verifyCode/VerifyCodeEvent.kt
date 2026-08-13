package com.example.assignment.ui.verifyCode

sealed class VerifyCodeEvent {
    data class CodeChanged(val value: String) : VerifyCodeEvent()
    data object VerifyClicked : VerifyCodeEvent()
    data object ResendClicked : VerifyCodeEvent()
    data object BackClicked : VerifyCodeEvent()
    data object NavigationHandled : VerifyCodeEvent()
}
