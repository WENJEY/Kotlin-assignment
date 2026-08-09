package com.example.assignment.navigation

sealed class ProfileRoutes(val route: String){
    data object Profile : ProfileRoutes("profile")

    data object Home : ProfileRoutes("home")
    data object UserProfile : ProfileRoutes("userProfile")
    data object ChangePassword : ProfileRoutes("changePassword")
    data object Feedback : ProfileRoutes("feedback")

}
