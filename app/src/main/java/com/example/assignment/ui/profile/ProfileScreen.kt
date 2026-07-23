package com.example.assignment.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.ui.navigation.ProfileRoutes
import com.example.assignment.ui.navigation.ScreenRoutes

@Composable
fun ProfileScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            when (route) {
                ScreenRoutes.Login.route -> {
                    navController.navigate(route) {
                        popUpTo(ProfileRoutes.Profile.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                ScreenRoutes.Home.route -> {
                    navController.navigate(route) {
                        popUpTo(ProfileRoutes.Profile.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }

                else -> {
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            }

            viewModel.onEvent(ProfileEvent.NavigationHandled)
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onEvent: (ProfileEvent) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        val isNarrowPhone = maxWidth < 360.dp
        val isVeryTallScreen = maxHeight > 1000.dp

        val horizontalPadding: Dp = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isNarrowPhone) 16.dp else 28.dp
            WindowWidthSizeClass.Medium -> 48.dp
            WindowWidthSizeClass.Expanded -> 64.dp
            else -> 28.dp
        }

        val bottomPadding = WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A4F84),
                            Color(0xFF1672B8),
                            Color(0xFF43B7E8)
                        )
                    )
                )
        )

        when (windowSize) {
            WindowWidthSizeClass.Compact -> ProfileCompactLayout(
                uiState = uiState,
                onEvent = onEvent,
                snackbarHostState = snackbarHostState,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen
            )

            WindowWidthSizeClass.Medium -> ProfileCompactLayout(
                uiState = uiState,
                onEvent = onEvent,
                snackbarHostState = snackbarHostState,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen
            )

            else -> ProfileCompactLayout(
                uiState = uiState,
                onEvent = onEvent,
                snackbarHostState = snackbarHostState,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen
            )
        }
    }
}
