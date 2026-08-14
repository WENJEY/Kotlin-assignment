package com.example.assignment.database.local

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment.ui.theme.DarkBackground
import com.example.assignment.ui.theme.DarkError
import com.example.assignment.ui.theme.DarkOnBackground
import com.example.assignment.ui.theme.DarkOnError
import com.example.assignment.ui.theme.DarkOnPrimary
import com.example.assignment.ui.theme.DarkOnPrimaryContainer
import com.example.assignment.ui.theme.DarkOnSurface
import com.example.assignment.ui.theme.DarkOnSurfaceVariant
import com.example.assignment.ui.theme.DarkOutline
import com.example.assignment.ui.theme.DarkPrimary
import com.example.assignment.ui.theme.DarkPrimaryContainer
import com.example.assignment.ui.theme.DarkSurface
import com.example.assignment.ui.theme.DarkSurfaceVariant
import com.example.assignment.ui.theme.LightBackground
import com.example.assignment.ui.theme.LightError
import com.example.assignment.ui.theme.LightOnBackground
import com.example.assignment.ui.theme.LightOnError
import com.example.assignment.ui.theme.LightOnPrimary
import com.example.assignment.ui.theme.LightOnPrimaryContainer
import com.example.assignment.ui.theme.LightOnSurface
import com.example.assignment.ui.theme.LightOnSurfaceVariant
import com.example.assignment.ui.theme.LightOutline
import com.example.assignment.ui.theme.LightPrimary
import com.example.assignment.ui.theme.LightPrimaryContainer
import com.example.assignment.ui.theme.LightSurface
import com.example.assignment.ui.theme.LightSurfaceVariant

@Entity(tableName = "theme_color_palettes")
data class ThemeColorPalette(
    @PrimaryKey
    val scheme: String,
    val background: Long,
    val onBackground: Long,
    val surface: Long,
    val onSurface: Long,
    val surfaceVariant: Long,
    val onSurfaceVariant: Long,
    val primary: Long,
    val onPrimary: Long,
    val primaryContainer: Long,
    val onPrimaryContainer: Long,
    val outline: Long,
    val error: Long,
    val onError: Long
) {
    companion object {
        const val LIGHT = "LIGHT"
        const val DARK = "DARK"

        fun lightDefaults(): ThemeColorPalette = ThemeColorPalette(
            scheme = LIGHT,
            background = LightBackground.toArgbLong(),
            onBackground = LightOnBackground.toArgbLong(),
            surface = LightSurface.toArgbLong(),
            onSurface = LightOnSurface.toArgbLong(),
            surfaceVariant = LightSurfaceVariant.toArgbLong(),
            onSurfaceVariant = LightOnSurfaceVariant.toArgbLong(),
            primary = LightPrimary.toArgbLong(),
            onPrimary = LightOnPrimary.toArgbLong(),
            primaryContainer = LightPrimaryContainer.toArgbLong(),
            onPrimaryContainer = LightOnPrimaryContainer.toArgbLong(),
            outline = LightOutline.toArgbLong(),
            error = LightError.toArgbLong(),
            onError = LightOnError.toArgbLong()
        )

        fun darkDefaults(): ThemeColorPalette = ThemeColorPalette(
            scheme = DARK,
            background = DarkBackground.toArgbLong(),
            onBackground = DarkOnBackground.toArgbLong(),
            surface = DarkSurface.toArgbLong(),
            onSurface = DarkOnSurface.toArgbLong(),
            surfaceVariant = DarkSurfaceVariant.toArgbLong(),
            onSurfaceVariant = DarkOnSurfaceVariant.toArgbLong(),
            primary = DarkPrimary.toArgbLong(),
            onPrimary = DarkOnPrimary.toArgbLong(),
            primaryContainer = DarkPrimaryContainer.toArgbLong(),
            onPrimaryContainer = DarkOnPrimaryContainer.toArgbLong(),
            outline = DarkOutline.toArgbLong(),
            error = DarkError.toArgbLong(),
            onError = DarkOnError.toArgbLong()
        )
    }
}

internal fun Color.toArgbLong(): Long = toArgb().toLong() and 0xFFFFFFFFL

internal fun Long.toComposeColor(): Color = Color(this.toInt())
