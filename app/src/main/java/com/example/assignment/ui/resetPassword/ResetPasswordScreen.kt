package com.example.assignment.ui.resetPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.theme.pageBackgroundBrush
import com.example.assignment.ui.register.RegisterEvent

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ResetPasswordViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            navController.navigate(ScreenRoutes.Login.route) {
                popUpTo(ScreenRoutes.ResetPassword.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            viewModel.onEvent(ResetPasswordEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.navigateToProfile) {
        if (uiState.navigateToProfile) {
            val popped = navController.popBackStack(
                ScreenRoutes.Profile.route,
                inclusive = false
            )
            if (!popped) {
                navController.navigate(ScreenRoutes.Profile.route) {
                    popUpTo(ScreenRoutes.ResetPassword.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            viewModel.onEvent(ResetPasswordEvent.NavigationHandled)
        }
    }

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
        val isNarrowPhone = maxWidth < 360.dp
        val isVeryTallScreen = maxHeight > 1000.dp
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

        val logoSize = when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if (isNarrowPhone) {
                    100.dp
                } else {
                    132.dp
                }
            }

            WindowWidthSizeClass.Medium -> 144.dp
            WindowWidthSizeClass.Expanded -> 188.dp
            else -> 144.dp
        }

        val formMaxWidth = when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if (isLandscape) {
                    480.dp
                } else {
                    Dp.Unspecified
                }
            }

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
                logoSize = logoSize,
                formMaxWidth = formMaxWidth,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                maxHeight = maxHeight,
                centerContent = isVeryTallScreen && !isLandscape,
                isLandscape = isLandscape
            )
            WindowWidthSizeClass.Medium -> MediumLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                formMaxWidth = formMaxWidth,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                centerContent = isVeryTallScreen
            )
            else -> ExpandedLayout(
                uiState = uiState,
                onEvent = onEvent,
                logoSize = logoSize,
                formMaxWidth = formMaxWidth,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                centerContent = isVeryTallScreen
            )
        }
        if (uiState.showSuccessDialog) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.surface,
                onDismissRequest = { onEvent(ResetPasswordEvent.ResetSuccessClicked) },
                title = { Text("Success") },
                text = { Text("Password updated successfully!") },
                confirmButton = {
                    TextButton(onClick = {
                        onEvent(ResetPasswordEvent.ResetSuccessClicked)
                    }) {
                        Text(
                            if (uiState.isChangePassword) "Back to Profile" else "Go to Login",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}