package com.example.assignment.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.assignment.database.remote.scanner.ScannedDocumentDao
import com.example.assignment.database.remote.scanner.ScannedDocumentEntity

@Database(
    entities = [ThemePreference::class, ThemeColorPalette::class, ScannedDocumentEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(ThemeModeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun themePreferenceDao(): ThemePreferenceDao
    abstract fun themeColorDao(): ThemeColorDao
    abstract fun scannedDocumentDao(): ScannedDocumentDao

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