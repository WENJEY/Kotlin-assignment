package com.example.assignment.ui.navigation

sealed class ScreenRoutes(val route: String) {
    data object Login : ScreenRoutes("login")
    data object Register : ScreenRoutes("register")
    data object Home : ScreenRoutes("home")
    data object Scanner : ScreenRoutes ("scanner")

    data object Profile : ScreenRoutes("profile")
    data object ChatBox : ScreenRoutes("chatbox")

}