package com.example.assignment.ui.appearance

import com.example.assignment.ui.theme.ThemeMode

data class AppearanceUiState(
    val selectedMode: ThemeMode = ThemeMode.SYSTEM,
    val savedMode: ThemeMode = ThemeMode.SYSTEM,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val navigateBack: Boolean = false
) {
    val hasUnsavedChanges: Boolean
        get() = selectedMode != savedMode
}
