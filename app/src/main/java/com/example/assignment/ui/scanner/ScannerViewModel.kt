package com.example.assignment.ui.scanner

import androidx.lifecycle.ViewModel
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.profile.ProfileTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class ScannerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ScannerUiState(
            greeting = greetingForHour(),
            recentDocuments = sampleDocuments
        )
    )
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun onEvent(event: ScannerEvent) {
        when (event) {
            ScannerEvent.ScanClicked -> showMessage("Scan will open the camera soon.")
            ScannerEvent.ImportClicked -> showMessage("PDF import is coming soon.")
            ScannerEvent.HistoryClicked -> showMessage("File history is coming soon.")
            ScannerEvent.ViewAllClicked -> showMessage("All documents will be listed here soon.")
            is ScannerEvent.DocumentClicked -> {
                val document = _uiState.value.recentDocuments.find { it.id == event.documentId }
                showMessage(document?.name ?: "Document unavailable.")
            }
            is ScannerEvent.TabSelected -> selectTab(event.tab)
            ScannerEvent.NavigationHandled -> _uiState.update { it.copy(navigateTo = null) }
            ScannerEvent.MessageShown -> _uiState.update { it.copy(message = null) }
        }
    }

    private fun selectTab(tab: ProfileTab) {
        if (tab == ProfileTab.Scanner || tab == ProfileTab.Logout) return

        val route = when (tab) {
            ProfileTab.Home -> ScreenRoutes.Home.route
            ProfileTab.ChatBox -> ScreenRoutes.ChatBox.route
            ProfileTab.Profile -> ScreenRoutes.Profile.route
            else -> null
        }

        _uiState.update {
            it.copy(
                selectedTab = tab,
                navigateTo = route
            )
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }
}

private fun greetingForHour(
    hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
): String = when (hour) {
    in 5..11 -> "Good morning! 👋"
    in 12..16 -> "Good afternoon! 👋"
    in 17..20 -> "Good evening! 👋"
    else -> "Good night! 👋"
}

private val sampleDocuments = listOf(
    RecentDocument(
        id = "1",
        name = "Contract Agreement.pdf",
        metadata = "Today, 2:30 PM  •  1.2 MB",
        type = DocumentType.PDF
    ),
    RecentDocument(
        id = "2",
        name = "Receipt_0424.jpg",
        metadata = "Today, 11:15 AM  •  450 KB",
        type = DocumentType.IMAGE
    ),
    RecentDocument(
        id = "3",
        name = "Meeting Notes.pdf",
        metadata = "Yesterday  •  1.8 MB",
        type = DocumentType.PDF
    )
)
