package com.example.assignment.ui.scanner

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
fun ScannerScreen(
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
            text = "Scanner",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
