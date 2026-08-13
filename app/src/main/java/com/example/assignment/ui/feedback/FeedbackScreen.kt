package com.example.assignment.ui.feedback

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.ScreenRoutes

@Composable
fun FeedbackScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: FeedbackViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(FeedbackEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        val message = uiState.successMessage ?: uiState.errorMessage
        if (message != null) {
            snackBarHostState.showSnackbar(message)
            viewModel.onEvent(FeedbackEvent.MessageShown)
        }
    }

    FeedbackLayout(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
        onNavigate = { route ->
            if (route == ScreenRoutes.Profile.route) {
                navController.popBackStack()
            } else {
                navController.navigate(route) {
                    popUpTo(ScreenRoutes.Profile.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
    )
}
