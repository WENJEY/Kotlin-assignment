package com.example.assignment.database.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment.ui.theme.ThemeMode

@Entity(tableName = "theme_preferences")
data class ThemePreference(
    @PrimaryKey
    val id: Int = SINGLETON_ID,
    val mode: ThemeMode
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}