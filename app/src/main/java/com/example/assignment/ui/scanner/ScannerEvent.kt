package com.example.assignment.ui.scanner

import com.example.assignment.ui.profile.ProfileTab

sealed class ScannerEvent {
    data object ScanClicked : ScannerEvent()
    data object ImportClicked : ScannerEvent()
    data object HistoryClicked : ScannerEvent()
    data object ViewAllClicked : ScannerEvent()
    data class DocumentClicked(val documentId: String) : ScannerEvent()
    data class TabSelected(val tab: ProfileTab) : ScannerEvent()
    data object NavigationHandled : ScannerEvent()
    data object MessageShown : ScannerEvent()
}
