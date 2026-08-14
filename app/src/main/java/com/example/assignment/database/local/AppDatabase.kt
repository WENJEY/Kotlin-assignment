package com.example.assignment.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ThemePreference::class, ThemeColorPalette::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(ThemeModeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun themePreferenceDao(): ThemePreferenceDao
    abstract fun themeColorDao(): ThemeColorDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "assignment.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
    }
}