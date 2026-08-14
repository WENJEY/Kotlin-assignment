package com.example.assignment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.assignment.database.remote.SupabaseClientProvider
import com.example.assignment.database.local.ThemeSettings
import com.example.assignment.navigation.MyAppNavHost
import com.example.assignment.ui.theme.AssignmentTheme
import com.example.assignment.ui.theme.DarkNavigationBar
import com.example.assignment.ui.theme.StoredThemePalettes
import com.example.assignment.ui.theme.ThemeMode
import com.example.assignment.ui.theme.appNavigationBarColor
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        SupabaseClientProvider.client.handleDeeplinks(intent)
        enableEdgeToEdge()

        setContent {
            val app = application as AssignmentApplication
            val settings by app.themeRepository.settings.collectAsState(
                initial = ThemeSettings(
                    mode = ThemeMode.SYSTEM,
                    palettes = StoredThemePalettes.defaults()
                )
            )
            val darkTheme = settings.mode.resolveDarkTheme(isSystemInDarkTheme())
            AssignmentTheme(
                darkTheme = darkTheme,
                palettes = settings.palettes
            ) {
                val navigationBarColor = appNavigationBarColor()
                SideEffect {
                    val navBarArgb = navigationBarColor.toArgb()
                    enableEdgeToEdge(
                        statusBarStyle = if (darkTheme) {
                            SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                        } else {
                            SystemBarStyle.light(
                                android.graphics.Color.TRANSPARENT,
                                android.graphics.Color.TRANSPARENT
                            )
                        },
                        navigationBarStyle = if (darkTheme) {
                            SystemBarStyle.dark(navBarArgb)
                        } else {
                            SystemBarStyle.light(navBarArgb, DarkNavigationBar.toArgb())
                        }
                    )
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                    @Suppress("DEPRECATION")
                    window.navigationBarColor = navBarArgb
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        window.isNavigationBarContrastEnforced = false
                    }
                }

                val windowSize = calculateWindowSizeClass(this)
                Box(modifier = Modifier.fillMaxSize()) {
                    MyAppNavHost(
                        windowSize = windowSize.widthSizeClass
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .background(navigationBarColor)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        SupabaseClientProvider.client.handleDeeplinks(intent)
    }
}
