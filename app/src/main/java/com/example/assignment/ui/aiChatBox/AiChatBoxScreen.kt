package com.example.assignment.ui.aiChatBox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment.ui.theme.pageBackgroundBrush

@Composable
fun AiChatBoxScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackgroundBrush()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "AI Chat Box",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
