/**
package com.example.assignment.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    windowSize = WindowWidthSizeClass,
    viewModel : HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Screen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }

    HomeScreenContent(
        uiState = uiState,
        windowSize = windowSize,
        onEvent = viewModel ::onEvent
    )
}

private fun HomeScreenContent(
    uiState : HomeUistate,
    windowSize : WindowWidthSizeCLass,
    onEvent : (HomeEvent) -> Unit
){

}
**/