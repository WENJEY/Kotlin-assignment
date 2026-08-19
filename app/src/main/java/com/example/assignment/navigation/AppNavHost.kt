package com.example.assignment.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.ui.login.LoginScreen
import com.example.assignment.ui.register.RegisterScreen
import com.example.assignment.ui.profile.ProfileScreen
import com.example.assignment.ui.userProfile.UserProfileScreen
import com.example.assignment.database.remote.SupabaseRepository
import com.example.assignment.navigation.ScreenRoutes.ForgotPassword
import com.example.assignment.ui.ChatBox.AiChatBoxScreen
import com.example.assignment.ui.appearance.AppearanceScreen
import com.example.assignment.ui.feedback.FeedbackScreen
import com.example.assignment.ui.forgotPassword.ForgotPasswordScreen
import com.example.assignment.ui.home.HomeScreen
import com.example.assignment.ui.resetPassword.ResetPasswordScreen
import com.example.assignment.ui.scanner.ScannerScreen
import com.example.assignment.ui.verifyCode.VerifyCodeScreen

@Composable
fun MyAppNavHost(
    windowSize: WindowWidthSizeClass
) {
    val navController = rememberNavController()

    val startDestination = when {
        SupabaseRepository().isLoggedIn() ->
            ScreenRoutes.Profile.route
        else ->
            ScreenRoutes.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenRoutes.Login.route) {
            LoginScreen(navController = navController, windowSize = windowSize)
        }
        composable(ScreenRoutes.Register.route) {
             RegisterScreen(navController = navController, windowSize = windowSize)
        }
        composable(ScreenRoutes.Home.route) {
            HomeScreen(navController = navController, windowSize = windowSize)
        }
        composable(ScreenRoutes.Scanner.route) {
            ScannerScreen(navController = navController, windowSize = windowSize)
        }
        composable(ScreenRoutes.ChatBox.route) {
            AiChatBoxScreen(navController = navController, windowSize = windowSize)
        }
        composable(ScreenRoutes.Profile.route) {
             ProfileScreen(navController = navController, windowSize = windowSize)
        }
        composable(ProfileRoutes.UserProfile.route) {
            UserProfileScreen(navController = navController, windowSize = windowSize)
        }
        composable(ProfileRoutes.Feedback.route) {
            FeedbackScreen(navController = navController, windowSize = windowSize)
        }
        composable(ProfileRoutes.Appearance.route) {
            AppearanceScreen(navController = navController, windowSize = windowSize)
        }
        composable(ForgotPassword.route){
             ForgotPasswordScreen(navController = navController, windowSize = windowSize)
        }
        composable(
            route = ScreenRoutes.VerifyCode.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = PasswordResetMode.Forgot
                }
            )
        ) {
            VerifyCodeScreen(navController = navController, windowSize = windowSize)
        }
        composable(
            route = ScreenRoutes.ResetPassword.route,
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = PasswordResetMode.Forgot
                }
            )
        ) {
            ResetPasswordScreen(navController = navController, windowSize = windowSize)
        }
    }
}

