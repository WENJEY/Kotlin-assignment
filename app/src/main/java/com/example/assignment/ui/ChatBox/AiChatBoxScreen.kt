package com.example.assignment.ui.ChatBox

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AiChatBoxScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: AiChatBoxViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(AiChatBoxEvent.ScreenOpened)
    }

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
            viewModel.onEvent(AiChatBoxEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(AiChatBoxEvent.MessageShown)
        }
    }

    AiChatBoxLayout(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
