package com.example.assignment

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.assignment.database.local.AppDatabase
import com.example.assignment.database.local.ThemeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class AssignmentApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    val themeRepository: ThemeRepository by lazy {
        ThemeRepository(
            preferenceDao = database.themePreferenceDao(),
            colorDao = database.themeColorDao()
        )
    }

    override fun onCreate() {
        super.onCreate()

        val savedTheme = runBlocking(Dispatchers.IO) {
            themeRepository.persistDefaultPalettes()
            themeRepository.getThemeMode()
        }
        AppCompatDelegate.setDefaultNightMode(savedTheme.toNightMode())
    }
}
