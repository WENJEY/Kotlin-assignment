package com.example.assignment.ui.scanner

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ScannerScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ScannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
            viewModel.onEvent(ScannerEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ScannerEvent.MessageShown)
        }
    }

    ScannerLayout(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
