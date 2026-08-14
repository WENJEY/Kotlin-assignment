package com.example.assignment.ui.appearance

import com.example.assignment.ui.theme.ThemeMode

sealed interface AppearanceEvent {
    data class ThemeSelected(val mode: ThemeMode) : AppearanceEvent
    data object SaveClicked : AppearanceEvent
    data object BackClicked : AppearanceEvent
    data object NavigationHandled : AppearanceEvent
    data object MessageShown : AppearanceEvent
}
