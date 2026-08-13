package com.example.assignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.assignment.database.SupabaseClientProvider
import com.example.assignment.navigation.MyAppNavHost
import com.example.assignment.ui.theme.AssignmentTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        SupabaseClientProvider.client.handleDeeplinks(intent)
        enableEdgeToEdge()

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = true
        }

        window.navigationBarColor = android.graphics.Color.WHITE

        setContent {
            AssignmentTheme {
                val windowSize = calculateWindowSizeClass(this)
                    MyAppNavHost(
                        windowSize = windowSize.widthSizeClass
                    )

            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        SupabaseClientProvider.client.handleDeeplinks(intent)
    }
}
