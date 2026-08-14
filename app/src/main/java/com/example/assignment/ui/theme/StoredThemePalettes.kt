package com.example.assignment.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.example.assignment.database.local.ThemeColorPalette
import com.example.assignment.database.local.toComposeColor

data class StoredThemePalettes(
    val light: ThemeColorPalette,
    val dark: ThemeColorPalette
) {
    fun lightColorScheme(): ColorScheme = light.toLightColorScheme()

    fun darkColorScheme(): ColorScheme = dark.toDarkColorScheme()

    companion object {
        fun defaults(): StoredThemePalettes = StoredThemePalettes(
            light = ThemeColorPalette.lightDefaults(),
            dark = ThemeColorPalette.darkDefaults()
        )
    }
}

private fun ThemeColorPalette.toLightColorScheme(): ColorScheme = lightColorScheme(
    primary = primary.toComposeColor(),
    onPrimary = onPrimary.toComposeColor(),
    primaryContainer = primaryContainer.toComposeColor(),
    onPrimaryContainer = onPrimaryContainer.toComposeColor(),
    background = background.toComposeColor(),
    onBackground = onBackground.toComposeColor(),
    surface = surface.toComposeColor(),
    onSurface = onSurface.toComposeColor(),
    surfaceVariant = surfaceVariant.toComposeColor(),
    onSurfaceVariant = onSurfaceVariant.toComposeColor(),
    outline = outline.toComposeColor(),
    error = error.toComposeColor(),
    onError = onError.toComposeColor()
)

private fun ThemeColorPalette.toDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = primary.toComposeColor(),
    onPrimary = onPrimary.toComposeColor(),
    primaryContainer = primaryContainer.toComposeColor(),
    onPrimaryContainer = onPrimaryContainer.toComposeColor(),
    background = background.toComposeColor(),
    onBackground = onBackground.toComposeColor(),
    surface = surface.toComposeColor(),
    onSurface = onSurface.toComposeColor(),
    surfaceVariant = surfaceVariant.toComposeColor(),
    onSurfaceVariant = onSurfaceVariant.toComposeColor(),
    outline = outline.toComposeColor(),
    error = error.toComposeColor(),
    onError = onError.toComposeColor()
)
