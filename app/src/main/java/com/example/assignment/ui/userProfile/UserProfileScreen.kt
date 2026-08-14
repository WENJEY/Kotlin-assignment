package com.example.assignment.ui.userProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.ui.theme.pageBackgroundBrush

@Composable
fun UserProfileScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: UserProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(UserProfileEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.onEvent(UserProfileEvent.MessageShown)
        }
        uiState.successMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.onEvent(UserProfileEvent.MessageShown)
        }
    }

    UserProfileScreenContent(
        uiState = uiState,
        windowSize = windowSize,
        snackBarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
        onAvatarSelected = viewModel::uploadProfileImage
    )
}

@Composable
private fun UserProfileScreenContent(
    uiState: UserProfileUiState,
    windowSize: WindowWidthSizeClass,
    snackBarHostState: SnackbarHostState,
    onEvent: (UserProfileEvent) -> Unit,
    onAvatarSelected: (ByteArray) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isNarrowPhone = maxWidth < 360.dp

        val horizontalPadding: Dp = when (windowSize) {
            WindowWidthSizeClass.Compact -> if (isNarrowPhone) 16.dp else 24.dp
            WindowWidthSizeClass.Medium -> 48.dp
            WindowWidthSizeClass.Expanded -> 64.dp
            else -> 24.dp
        }

        val avatarSize = when (windowSize) {
            WindowWidthSizeClass.Compact -> 100.dp
            WindowWidthSizeClass.Medium -> 120.dp
            WindowWidthSizeClass.Expanded -> 140.dp
            else -> 100.dp
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundBrush())
        )

        // Compact only for now; medium/expanded reuse the same layout.
        UserProfileCompactLayout(
            uiState = uiState,
            onEvent = onEvent,
            onAvatarSelected = onAvatarSelected,
            snackBarHostState = snackBarHostState,
            horizontalPadding = horizontalPadding,
            avatarSize = avatarSize
        )
    }
}
