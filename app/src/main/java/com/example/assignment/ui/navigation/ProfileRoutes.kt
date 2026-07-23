package com.example.assignment.ui.navigation

sealed class ProfileRoutes(val route: String){
    data object Profile : ProfileRoutes("profile")

    data object Home : ProfileRoutes("home")
    data object UserProfile : ProfileRoutes("userProfile")
    data object ChangePassword : ProfileRoutes("changePassword")
    data object Feedback : ProfileRoutes("feedback")
    data object Logout : ProfileRoutes("logout")
}
