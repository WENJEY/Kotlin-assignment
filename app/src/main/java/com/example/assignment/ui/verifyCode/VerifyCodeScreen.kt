package com.example.assignment.ui.verifyCode

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.PasswordResetMode
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.theme.AssignmentTheme
import com.example.assignment.ui.theme.pageBackgroundBrush

@Composable
fun VerifyCodeScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: VerifyCodeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(VerifyCodeEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.navigateToResetPassword) {
        if (uiState.navigateToResetPassword) {
            navController.navigate(ScreenRoutes.ResetPassword.createRoute(uiState.mode)) {
                if (uiState.mode == PasswordResetMode.Change) {
                    popUpTo(ScreenRoutes.VerifyCode.route) {
                        inclusive = true
                    }
                } else {
                    popUpTo(ScreenRoutes.ForgotPassword.route) {
                        inclusive = true
                    }
                }
                launchSingleTop = true
            }
            viewModel.onEvent(VerifyCodeEvent.NavigationHandled)
        }
    }

    VerifyCodeContent(
        uiState = uiState,
        windowSize = windowSize,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun VerifyCodeContent(
    uiState: VerifyCodeUiState,
    windowSize: WindowWidthSizeClass,
    onEvent: (VerifyCodeEvent) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        val isNarrowPhone = maxWidth < 360.dp
        val isVeryTallScreen = maxHeight > 1000.dp

        val horizontalPadding = when (windowSize) {
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

        val logoSize = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isNarrowPhone) 108.dp else 140.dp
            WindowWidthSizeClass.Medium -> 144.dp
            WindowWidthSizeClass.Expanded -> 188.dp
            else -> 144.dp
        }

        val formMaxWidth = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isLandscape) 480.dp else Dp.Unspecified
            WindowWidthSizeClass.Medium -> 560.dp
            WindowWidthSizeClass.Expanded -> 720.dp
            else -> Dp.Unspecified
        }

        val bottomPadding = WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundBrush())
        )

        when (windowSize) {
            WindowWidthSizeClass.Compact -> CompactLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = if (isLandscape) logoSize * 0.72f else logoSize,
                titleSize = if (isLandscape) titleSize * 0.85f else titleSize,
                formMaxWidth = formMaxWidth,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                centerContent = isVeryTallScreen && !isLandscape,
                isLandscape = isLandscape
            )
            WindowWidthSizeClass.Medium -> MediumLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                titleSize = titleSize,
                formMaxWidth = formMaxWidth,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                centerContent = isVeryTallScreen
            )
            else -> ExpandedLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                titleSize = titleSize,
                formMaxWidth = formMaxWidth,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                centerContent = isVeryTallScreen
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {
    AssignmentTheme {
        VerifyCodeContent(
            uiState = VerifyCodeUiState(
                email = "user@example.com",
                code = "123456"
            ),
            windowSize = WindowWidthSizeClass.Expanded,
            onEvent = {}
        )
    }
}
