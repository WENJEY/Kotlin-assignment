package com.example.assignment.navigation

import android.net.Uri
import androidx.navigation.NavController

sealed class ScreenRoutes(val route: String) {
    data object Login : ScreenRoutes("login")
    data object Register : ScreenRoutes("register")

    data object Home : ScreenRoutes("home")
    data object Scanner : ScreenRoutes ("scanner")
    data object Profile : ScreenRoutes("profile")
    data object ChatBox : ScreenRoutes("chatbox")
    data object ForgotPassword : ScreenRoutes ("forgetPassword")
    data object VerifyCode : ScreenRoutes("verifyCode/{email}?mode={mode}") {
        fun createRoute(
            email: String,
            mode: String = PasswordResetMode.Forgot
        ): String = "verifyCode/${Uri.encode(email.trim())}?mode=$mode"
    }

    data object ResetPassword : ScreenRoutes("resetPassword?mode={mode}") {
        fun createRoute(mode: String = PasswordResetMode.Forgot): String =
            "resetPassword?mode=$mode"
    }

}

object PasswordResetMode {
    const val Forgot = "forgot"
    const val Change = "change"
}

fun NavController.navigateToLoginAndClear() {
    navigate(ScreenRoutes.Login.route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
