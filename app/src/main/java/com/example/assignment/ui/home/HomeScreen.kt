package com.example.assignment.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.navigation.navigateToLoginAndClear
import com.example.assignment.ui.profile.LogoutConfirmDialog
import com.example.assignment.ui.theme.pageBackgroundBrush

@Composable
fun HomeScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(HomeEvent.ScreenOpened)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            if (route == ScreenRoutes.Login.route) {
                navController.navigateToLoginAndClear()
            } else {
                val currentRoute = navController.currentDestination?.route
                navController.navigate(route) {
                    launchSingleTop = true
                    if (currentRoute != null) {
                        popUpTo(currentRoute) { inclusive = true }
                    }
                }
            }
            viewModel.onEvent(HomeEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(HomeEvent.MessageShown)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackgroundBrush())
    ) {
        HomeLayout(
            uiState = uiState,
            windowSize = windowSize,
            snackbarHostState = snackbarHostState,
            onEvent = viewModel::onEvent
        )
        if (uiState.showLogoutDialog) {
            LogoutConfirmDialog(
                onConfirm = { viewModel.onEvent(HomeEvent.LogoutConfirmed) },
                onDismiss = { viewModel.onEvent(HomeEvent.LogoutCanceled) }
            )
        }
    }
}
