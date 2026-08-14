package com.example.assignment.ui.theme

import androidx.appcompat.app.AppCompatDelegate

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    fun toNightMode(): Int = when (this) {
        LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        DARK -> AppCompatDelegate.MODE_NIGHT_YES
        SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun resolveDarkTheme(systemInDarkTheme: Boolean): Boolean = when (this) {
        LIGHT -> false
        DARK -> true
        SYSTEM -> systemInDarkTheme
    }
}
