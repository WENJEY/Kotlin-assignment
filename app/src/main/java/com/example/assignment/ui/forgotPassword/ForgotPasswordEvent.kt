package com.example.assignment.ui.forgotPassword
/**package com.example.assignment.ui.forgotPassword

sealed class ForgotPasswordEvent {
    data class EmailChanged(val value : String) : ForgotPasswordEvent()

    data object BackToLoginClicked : ForgotPasswordEvent();

    data object SendResetLinkClicked : ForgotPasswordEvent();

    data object NavigationHandled : ForgotPasswordEvent();
}

 **/

