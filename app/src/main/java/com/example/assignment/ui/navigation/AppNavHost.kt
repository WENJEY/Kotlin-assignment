package com.example.assignment.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.ui.login.LoginScreen
import com.example.assignment.ui.register.RegisterScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyAppNavHost(windowSize: WindowWidthSizeClass) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    FirebaseAuth.getInstance().signOut()

    // Check already login or not
    val startDestination = if (auth.currentUser != null) {
        ScreenRoutes.Home.route
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
        composable(ScreenRoutes.Home.route) {
            // HomeScreen(navController = navController, windowSize = windowSize)
        }
    }
}
