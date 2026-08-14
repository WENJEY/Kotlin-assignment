package com.example.assignment.database.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeColorDao {
    @Query("SELECT * FROM theme_color_palettes")
    fun observePalettes(): Flow<List<ThemeColorPalette>>

    @Query("SELECT * FROM theme_color_palettes")
    suspend fun getPalettes(): List<ThemeColorPalette>

    @Upsert
    suspend fun upsertAll(palettes: List<ThemeColorPalette>)
}