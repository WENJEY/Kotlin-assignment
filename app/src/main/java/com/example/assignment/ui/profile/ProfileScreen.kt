package com.example.assignment.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.theme.DialogScrim
import com.example.assignment.ui.theme.LogoutRed
import com.example.assignment.ui.theme.pageBackgroundBrush

@Composable
fun ProfileScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Reload from Supabase when returning to the screen so phone/emulator stay in sync.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(ProfileEvent.LoadProfile)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            when (route) {
                ScreenRoutes.Login.route -> {
                    navController.navigate(route) {
                        popUpTo(ScreenRoutes.Profile.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                ScreenRoutes.Home.route,
                ScreenRoutes.Scanner.route,
                ScreenRoutes.ChatBox.route -> {
                    val currentRoute = navController.currentDestination?.route
                    navController.navigate(route) {
                        launchSingleTop = true
                        if (currentRoute != null) {
                            popUpTo(currentRoute) { inclusive = true }
                        }
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

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.onEvent(ProfileEvent.ErrorShown)
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        windowSize = windowSize,
        snackBarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
        onAvatarSelected = viewModel::uploadProfileImage
    )
}

@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    windowSize: WindowWidthSizeClass,
    snackBarHostState: SnackbarHostState,
    onEvent: (ProfileEvent) -> Unit,
    onAvatarSelected: (ByteArray) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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

        val avatarSize = when (windowSize) {
            WindowWidthSizeClass.Compact -> 110.dp
            WindowWidthSizeClass.Medium -> 140.dp
            WindowWidthSizeClass.Expanded -> 160.dp
            else -> 120.dp
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundBrush())
        )

        when (windowSize) {
            WindowWidthSizeClass.Compact -> ProfileCompactLayout(
                uiState = uiState,
                onEvent = onEvent,
                onAvatarSelected = onAvatarSelected,
                snackBarHostState = snackBarHostState,
                horizontalPadding = horizontalPadding,
                bottomPadding = bottomPadding,
                avatarSize = avatarSize,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen,
            )

            WindowWidthSizeClass.Medium -> ProfileMediumLayout(
                uiState = uiState,
                onEvent = onEvent,
                onAvatarSelected = onAvatarSelected,
                snackBarHostState = snackBarHostState,
                horizontalPadding = horizontalPadding,
                avatarSize = avatarSize,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen,
            )

            WindowWidthSizeClass.Expanded -> ProfileExpandedLayout(
                uiState = uiState,
                onEvent = onEvent,
                onAvatarSelected = onAvatarSelected,
                snackBarHostState = snackBarHostState,
                horizontalPadding = horizontalPadding,
                avatarSize = avatarSize,
                isLandscape = isLandscape,
                centerContent = isVeryTallScreen,
            )
        }
    }
    if (uiState.isSendingChangePassword) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DialogScrim),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
    if (uiState.showLogoutDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { onEvent(ProfileEvent.LogoutCanceled) },
            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to log out?")
            },
            confirmButton = {
                TextButton(onClick = { onEvent(ProfileEvent.LogoutConfirmed) }) {
                    Text("Logout", color = LogoutRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(ProfileEvent.LogoutCanceled) }) {
                    Text("Cancel")
                }
            }
        )
    }
    }
}
