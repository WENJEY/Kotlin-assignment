package com.example.assignment.ui.appearance

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
fun AppearanceScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: AppearanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(AppearanceEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        val message = uiState.successMessage ?: uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(AppearanceEvent.MessageShown)
        }
    }

    AppearanceLayout(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
