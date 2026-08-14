package com.example.assignment.database.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemePreferenceDao {
    @Query("SELECT * FROM theme_preferences WHERE id = 1 LIMIT 1")
    fun observePreference(): Flow<ThemePreference?>

    @Query("SELECT * FROM theme_preferences WHERE id = 1 LIMIT 1")
    suspend fun getPreference(): ThemePreference?

    @Upsert
    suspend fun upsert(preference: ThemePreference)
}