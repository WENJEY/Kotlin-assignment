package com.example.assignment.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.ui.login.LoginScreen
import com.example.assignment.ui.register.RegisterScreen
import com.example.assignment.ui.profile.ProfileScreen
import com.example.assignment.database.SupabaseRepository

@Composable
fun MyAppNavHost(windowSize: WindowWidthSizeClass) {
    val navController = rememberNavController()
    // Check already login or not
    val startDestination = if (SupabaseRepository().isLoggedIn()) {
        ScreenRoutes.Profile.route
    } else {
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
        /**composable(ScreenRoutes.Home.route) {
             HomeScreen(navController = navController, windowSize = windowSize)
        }
        composable(ScreenRoutes.Scanner.route){
            // ScannerScreen(navController = navController, windowSize = windowSize)
        }**/
        composable(ScreenRoutes.Profile.route){
             ProfileScreen(navController = navController, windowSize = windowSize)
        }
        /**
        composable (ScreenRoutes.ChatBox.route){
             ChatBoxScreen (navController = navController , windowSize = windowSize)
        }**/
    }
}
