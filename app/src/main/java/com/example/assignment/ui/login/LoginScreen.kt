package com.example.assignment.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.ui.navigation.ScreenRoutes

@Composable
fun LoginScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            if (route == ScreenRoutes.Home) {
                navController.navigate(route.route) {
                    popUpTo(ScreenRoutes.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                navController.navigate(route.route)
            }
            viewModel.onEvent(LoginEvent.NavigationHandled)
        }
    }

    LoginScreenContent(
        uiState = uiState,
        windowSize = windowSize,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    windowSize: WindowWidthSizeClass,
    onEvent: (LoginEvent) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        val isNarrowPhone = maxWidth < 360.dp
        val isVeryTallScreen = maxHeight > 1000.dp

        val horizontalPadding: Dp = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isNarrowPhone) 16.dp else 24.dp
            WindowWidthSizeClass.Medium -> 48.dp
            WindowWidthSizeClass.Expanded -> 64.dp
            else -> 24.dp
        }

        val titleSize: TextUnit = when {
            maxWidth < 340.dp -> 27.sp
            windowSize == WindowWidthSizeClass.Compact -> 34.sp
            windowSize == WindowWidthSizeClass.Medium -> 40.sp
            else -> 36.sp
        }

        val logoSize: Dp = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isNarrowPhone) 108.dp else 140.dp
            WindowWidthSizeClass.Medium -> 144.dp
            WindowWidthSizeClass.Expanded -> 188.dp
            else -> 144.dp
        }

        val formMaxWidth: Dp = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isLandscape) 480.dp else Dp.Unspecified
            WindowWidthSizeClass.Medium -> 520.dp
            WindowWidthSizeClass.Expanded -> 480.dp
            else -> Dp.Unspecified
        }

        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

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
            WindowWidthSizeClass.Compact -> CompactLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                titleSize = titleSize,
                formMaxWidth = formMaxWidth,
                stackHeader = isNarrowPhone || isLandscape,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                maxHeight = maxHeight,
                maxWidth = maxWidth,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen,
            )

            WindowWidthSizeClass.Medium -> MediumLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                titleSize = titleSize,
                formMaxWidth = formMaxWidth,
                bottomPadding = bottomPadding,
                maxHeight = maxHeight,
                maxWidth = maxWidth,
                centerContent = isVeryTallScreen,
            )

            else -> ExpandedLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                titleSize = titleSize,
                formMaxWidth = formMaxWidth,
                bottomPadding = bottomPadding,
                maxHeight = maxHeight,
                maxWidth = maxWidth,
                centerContent = isVeryTallScreen,
            )
        }
    }
}