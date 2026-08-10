package com.example.assignment.ui.resetPassword

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
import androidx.compose.runtime.collectAsState
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
import com.example.assignment.navigation.ScreenRoutes

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ResetPasswordViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    // ---------------------------------------------------------
    // HANDLE BACK TO LOGIN
    // ---------------------------------------------------------

    LaunchedEffect(uiState.navigateToLogin) {

        if (uiState.navigateToLogin) {

            navController.navigate(ScreenRoutes.Login.route) {

                popUpTo(ScreenRoutes.ResetPassword.route) {
                    inclusive = true
                }

                launchSingleTop = true
            }

            viewModel.onEvent(
                ResetPasswordEvent.NavigationHandled
            )
        }
    }


    // ---------------------------------------------------------
    // SCREEN CONTENT
    // ---------------------------------------------------------

    ResetPasswordContent(
        uiState = uiState,
        windowSize = windowSize,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ResetPasswordContent(
    uiState: ResetPasswordUiState,
    windowSize: WindowWidthSizeClass,
    onEvent: (ResetPasswordEvent) -> Unit
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val isLandscape = maxWidth > maxHeight

        val isNarrowPhone =
            maxWidth < 360.dp

        val isVeryTallScreen =
            maxHeight > 1000.dp


        // ---------------------------------------------------------
        // HORIZONTAL PADDING
        // Same as Forgot Password
        // ---------------------------------------------------------

        val horizontalPadding = when (windowSize) {

            WindowWidthSizeClass.Compact -> {

                if (isNarrowPhone) {
                    16.dp
                } else {
                    24.dp
                }
            }

            WindowWidthSizeClass.Medium -> 48.dp

            WindowWidthSizeClass.Expanded -> 64.dp

            else -> 24.dp
        }


        // ---------------------------------------------------------
        // TITLE SIZE
        // Same as Forgot Password
        // ---------------------------------------------------------

        val titleSize: TextUnit = when {

            maxWidth < 340.dp ->
                27.sp

            windowSize ==
                    WindowWidthSizeClass.Compact ->
                34.sp

            windowSize ==
                    WindowWidthSizeClass.Medium ->
                40.sp

            else ->
                36.sp
        }


        // ---------------------------------------------------------
        // LOGO / LOCK SIZE
        // Same sizing logic
        // ---------------------------------------------------------

        val logoSize = when (windowSize) {

            WindowWidthSizeClass.Compact -> {

                if (isNarrowPhone) {
                    108.dp
                } else {
                    140.dp
                }
            }

            WindowWidthSizeClass.Medium ->
                144.dp

            WindowWidthSizeClass.Expanded ->
                188.dp

            else ->
                144.dp
        }


        // ---------------------------------------------------------
        // FORM MAX WIDTH
        // Same as Forgot Password
        // ---------------------------------------------------------

        val formMaxWidth = when (windowSize) {

            WindowWidthSizeClass.Compact -> {

                if (isLandscape) {
                    480.dp
                } else {
                    Dp.Unspecified
                }
            }

            WindowWidthSizeClass.Medium ->
                520.dp

            WindowWidthSizeClass.Expanded ->
                480.dp

            else ->
                Dp.Unspecified
        }


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION BAR PADDING
        // ---------------------------------------------------------

        val bottomPadding =
            WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()


        // ---------------------------------------------------------
        // BLUE BACKGROUND
        // Same as Forgot Password
        // ---------------------------------------------------------

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


        // ---------------------------------------------------------
        // RESPONSIVE LAYOUT
        // ---------------------------------------------------------

        when (windowSize) {

            // =====================================================
            // COMPACT
            // =====================================================

            WindowWidthSizeClass.Compact -> {

                CompactLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    maxHeight = maxHeight,
                    maxWidth = maxWidth,
                    centerContent = isVeryTallScreen
                )
            }


            // =====================================================
            // MEDIUM
            // TEMPORARILY USE COMPACT
            // =====================================================

            WindowWidthSizeClass.Medium -> {

                CompactLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    maxHeight = maxHeight,
                    maxWidth = maxWidth,
                    centerContent = isVeryTallScreen
                )
            }


            WindowWidthSizeClass.Expanded -> {

                CompactLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    maxHeight = maxHeight,
                    maxWidth = maxWidth,
                    centerContent = isVeryTallScreen
                )
            }


            else -> {
                CompactLayout(
                    uiState = uiState,
                    onEvent = onEvent,
                    logoSize = logoSize,
                    titleSize = titleSize,
                    formMaxWidth = formMaxWidth,
                    horizontalPadding = horizontalPadding,
                    bottomPadding = bottomPadding,
                    maxHeight = maxHeight,
                    maxWidth = maxWidth,
                    centerContent = isVeryTallScreen
                )
            }
        }
    }
}