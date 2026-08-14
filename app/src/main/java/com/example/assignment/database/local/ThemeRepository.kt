package com.example.assignment.database.local

import com.example.assignment.ui.theme.StoredThemePalettes
import com.example.assignment.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ThemeRepository(
    private val preferenceDao: ThemePreferenceDao,
    private val colorDao: ThemeColorDao
) {
    val themeMode: Flow<ThemeMode> =
        preferenceDao.observePreference().map { it?.mode ?: ThemeMode.SYSTEM }

    val palettes: Flow<StoredThemePalettes> =
        colorDao.observePalettes().map { it.toStoredPalettes() }

    val settings: Flow<ThemeSettings> = combine(themeMode, palettes) { mode, storedPalettes ->
        ThemeSettings(mode = mode, palettes = storedPalettes)
    }

    suspend fun getThemeMode(): ThemeMode =
        preferenceDao.getPreference()?.mode ?: ThemeMode.SYSTEM

    suspend fun getPalettes(): StoredThemePalettes =
        colorDao.getPalettes().toStoredPalettes()

    suspend fun saveThemeMode(mode: ThemeMode) {
        preferenceDao.upsert(ThemePreference(mode = mode))
        persistDefaultPalettes()
    }

    suspend fun persistDefaultPalettes() {
        colorDao.upsertAll(
            listOf(
                ThemeColorPalette.lightDefaults(),
                ThemeColorPalette.darkDefaults()
            )
        )
    }

    private fun List<ThemeColorPalette>.toStoredPalettes(): StoredThemePalettes {
        val light = firstOrNull { it.scheme == ThemeColorPalette.LIGHT }
            ?: ThemeColorPalette.lightDefaults()
        val dark = firstOrNull { it.scheme == ThemeColorPalette.DARK }
            ?: ThemeColorPalette.darkDefaults()
        return StoredThemePalettes(light = light, dark = dark)
    }
}

data class ThemeSettings(
    val mode: ThemeMode,
    val palettes: StoredThemePalettes
)
