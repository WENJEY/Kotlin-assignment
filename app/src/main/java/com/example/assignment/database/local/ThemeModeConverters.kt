package com.example.assignment.database.local

import androidx.room.TypeConverter
import com.example.assignment.ui.theme.ThemeMode

class ThemeModeConverters {
    @TypeConverter
    fun fromThemeMode(mode: ThemeMode): String = mode.name

    @TypeConverter
    fun toThemeMode(value: String): ThemeMode =
        ThemeMode.entries.firstOrNull { it.name == value } ?: ThemeMode.SYSTEM
}