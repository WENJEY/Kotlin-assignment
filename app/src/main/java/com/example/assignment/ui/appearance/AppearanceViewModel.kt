package com.example.assignment.ui.appearance

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.AssignmentApplication
import com.example.assignment.database.local.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppearanceViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ThemeRepository =
        (application as AssignmentApplication).themeRepository

    private val _uiState = MutableStateFlow(AppearanceUiState())
    val uiState: StateFlow<AppearanceUiState> = _uiState.asStateFlow()

    init {
        observeSavedTheme()
    }

    fun onEvent(event: AppearanceEvent) {
        when (event) {
            is AppearanceEvent.ThemeSelected -> {
                _uiState.update {
                    it.copy(
                        selectedMode = event.mode,
                        successMessage = null,
                        errorMessage = null
                    )
                }
            }

            AppearanceEvent.SaveClicked -> saveTheme()
            AppearanceEvent.BackClicked ->
                _uiState.update { it.copy(navigateBack = true) }

            AppearanceEvent.NavigationHandled ->
                _uiState.update { it.copy(navigateBack = false) }

            AppearanceEvent.MessageShown ->
                _uiState.update {
                    it.copy(successMessage = null, errorMessage = null)
                }
        }
    }

    private fun observeSavedTheme() {
        viewModelScope.launch {
            repository.themeMode
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "Unable to load appearance settings."
                        )
                    }
                }
                .collect { mode ->
                    _uiState.update {
                        it.copy(
                            selectedMode = mode,
                            savedMode = mode,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun saveTheme() {
        if (_uiState.value.isSaving || _uiState.value.isLoading) return

        val mode = _uiState.value.selectedMode
        _uiState.update {
            it.copy(
                isSaving = true,
                successMessage = null,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                repository.saveThemeMode(mode)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        savedMode = mode,
                        isSaving = false,
                        successMessage = "Appearance settings saved."
                    )
                }
                AppCompatDelegate.setDefaultNightMode(mode.toNightMode())
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message
                            ?: "Unable to save appearance settings."
                    )
                }
            }
        }
    }
}
