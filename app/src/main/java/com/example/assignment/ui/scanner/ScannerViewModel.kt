package com.example.assignment.ui.scanner

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.database.remote.scanner.ScannerRepository
import com.example.assignment.database.remote.ChatBox.ChatRepository
import com.example.assignment.database.remote.Repository
import com.example.assignment.database.remote.supabase.SupabaseRepository
import com.example.assignment.navigation.PendingScreenAction
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.ui.profile.ProfileTab
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class ScannerViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = ScannerRepository(application)
    private val chatRepository = ChatRepository()
    private val authRepository: Repository = SupabaseRepository()
    private var observingDocuments = false
    private val _uiState = MutableStateFlow(
        ScannerUiState(greeting = greetingForHour())
    )
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun onEvent(event: ScannerEvent) {
        when (event) {
            ScannerEvent.ScreenOpened -> {
                when (PendingScreenAction.consumeScannerLaunch()) {
                    PendingScreenAction.SCAN -> requestLaunch(ScannerLaunchAction.DocumentScanner)
                    PendingScreenAction.IMPORT -> requestLaunch(ScannerLaunchAction.Import)
                    PendingScreenAction.HISTORY -> openHistory()
                }
                observeDocuments()
            }
            ScannerEvent.ScanClicked -> requestLaunch(ScannerLaunchAction.DocumentScanner)
            ScannerEvent.ImportClicked -> requestLaunch(ScannerLaunchAction.Import)
            ScannerEvent.HistoryClicked,
            ScannerEvent.ViewAllClicked -> openHistory()
            is ScannerEvent.DocumentClicked -> openDocument(event.documentId)
            is ScannerEvent.ShareClicked -> shareDocument(event.documentId)
            is ScannerEvent.DeleteClicked -> _uiState.update { it.copy(confirmDeleteId = event.documentId) }
            ScannerEvent.DeleteConfirmed -> deleteConfirmed()
            ScannerEvent.DeleteCanceled -> _uiState.update { it.copy(confirmDeleteId = null) }
            ScannerEvent.CopyTextClicked -> copyExtractedText()
            ScannerEvent.RecheckClicked -> {
                val document = _uiState.value.selectedDocument ?: return
                checkValidity(document)
            }
            ScannerEvent.BackClicked -> goBack()
            ScannerEvent.LaunchActionHandled -> _uiState.update {
                it.copy(launchAction = ScannerLaunchAction.None)
            }
            ScannerEvent.DocumentScannerUnavailable -> requestLaunch(ScannerLaunchAction.Camera)
            is ScannerEvent.ScanCaptured -> saveScan(event.imageUris, event.pdfUri)
            is ScannerEvent.CameraImageCaptured -> saveScan(listOf(event.uri), pdfUri = null)
            is ScannerEvent.FileImported -> saveImport(event.uri)
            ScannerEvent.CameraPermissionDenied -> showMessage("Camera permission is needed to scan documents.")
            ScannerEvent.ShareHandled -> _uiState.update { it.copy(shareRequest = null) }
            is ScannerEvent.TabSelected -> selectTab(event.tab)
            ScannerEvent.LogoutConfirmed -> confirmLogout()
            ScannerEvent.LogoutCanceled -> _uiState.update { it.copy(showLogoutDialog = false) }
            ScannerEvent.NavigationHandled -> _uiState.update { it.copy(navigateTo = null) }
            ScannerEvent.MessageShown -> _uiState.update { it.copy(message = null) }
        }
    }

    private fun observeDocuments() {
        if (observingDocuments) return
        observingDocuments = true
        viewModelScope.launch {
            when (val result = repository.syncWithCloud()) {
                is Result.Error -> showMessage(result.message)
                is Result.Success -> Unit
            }
        }
        viewModelScope.launch {
            repository.observeDocuments().collect { documents ->
                _uiState.update { state ->
                    val selected = documents.find { it.id == state.selectedDocument?.id }
                    state.copy(
                        documents = documents,
                        selectedDocument = selected,
                        destination = if (
                            state.destination == ScannerPage.Detail && selected == null
                        ) {
                            if (state.openedFromHistory) ScannerPage.History else ScannerPage.Home
                        } else {
                            state.destination
                        }
                    )
                }
            }
        }
    }

    private fun requestLaunch(action: ScannerLaunchAction) {
        _uiState.update { it.copy(launchAction = action) }
    }

    private fun openHistory() {
        _uiState.update {
            it.copy(
                destination = ScannerPage.History,
                openedFromHistory = true,
                selectedDocument = null
            )
        }
    }

    private fun openDocument(documentId: String) {
        val document = _uiState.value.documents.find { it.id == documentId }
        if (document == null) {
            showMessage("Document unavailable.")
            return
        }
        _uiState.update {
            it.copy(
                selectedDocument = document,
                openedFromHistory = it.destination == ScannerPage.History,
                destination = ScannerPage.Detail
            )
        }
    }

    private fun saveScan(
        imageUris: List<android.net.Uri>,
        pdfUri: android.net.Uri?
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isProcessing = true, processingMessage = "Reading scanned document...")
            }
            when (
                val result = repository.saveScan(
                    imageUris = imageUris,
                    pdfUri = pdfUri,
                    source = DocumentSource.SCAN
                )
            ) {
                is Result.Success -> showSaved(result.data)
                is Result.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    showMessage(result.message)
                }
            }
        }
    }

    private fun saveImport(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isProcessing = true, processingMessage = "Reading imported file...")
            }
            when (val result = repository.saveImportedUri(uri, DocumentSource.IMPORT)) {
                is Result.Success -> showSaved(result.data)
                is Result.Error -> {
                    _uiState.update { it.copy(isProcessing = false) }
                    showMessage(result.message)
                }
            }
        }
    }

    private fun showSaved(document: ScannedDocument) {
        _uiState.update {
            it.copy(
                selectedDocument = document,
                destination = ScannerPage.Detail,
                openedFromHistory = false
            )
        }
        checkValidity(document)
    }

    private fun checkValidity(document: ScannedDocument) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessing = true,
                    processingMessage = "Checking if this document is valid, then Malaysian labour law..."
                )
            }
            repository.saveValidation(
                id = document.id,
                isValid = null,
                status = DocumentValidationStatus.CHECKING,
                documentKind = document.documentKind,
                summary = document.validationSummary,
                issues = document.validationIssues,
                legalStatus = DocumentLegalStatus.CHECKING
            )

            val extracted = document.extractedText.trim()
            if (extracted.length < 20) {
                repository.saveValidation(
                    id = document.id,
                    isValid = false,
                    status = DocumentValidationStatus.UNREADABLE,
                    documentKind = "unreadable",
                    summary = "Not enough readable text was found to check this document.",
                    issues = listOf("The scan did not contain enough text."),
                    isLegal = false,
                    legalStatus = DocumentLegalStatus.SKIPPED,
                    legalSummary = "Labour-law check was skipped because the document could not be read."
                )
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        message = "Saved, but the document could not be checked."
                    )
                }
                return@launch
            }

            when (val result = chatRepository.validateDocument(extracted)) {
                is Result.Success -> {
                    val data = result.data
                    val status = when {
                        data.documentType.equals("unreadable", ignoreCase = true) -> {
                            DocumentValidationStatus.UNREADABLE
                        }
                        data.valid -> DocumentValidationStatus.VALID
                        else -> DocumentValidationStatus.INVALID
                    }
                    val legalStatus = legalStatusFrom(data)
                    repository.saveValidation(
                        id = document.id,
                        isValid = data.valid,
                        status = status,
                        documentKind = data.documentType,
                        summary = data.summary,
                        issues = data.issues,
                        isLegal = data.legal,
                        legalStatus = legalStatus,
                        legalStatute = data.statute,
                        legalSummary = data.legalSummary,
                        violations = data.violations,
                        missingRequirements = data.missingRequirements,
                        nextSteps = data.nextSteps
                    )
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            message = when {
                                status != DocumentValidationStatus.VALID -> {
                                    "This is not a valid employment document."
                                }
                                legalStatus == DocumentLegalStatus.COMPLIANT -> {
                                    "Valid, and the terms appear to meet Malaysian labour law."
                                }
                                else -> {
                                    "Valid document, but the terms may not meet Malaysian labour law."
                                }
                            }
                        )
                    }
                }
                is Result.Error -> {
                    repository.saveValidation(
                        id = document.id,
                        isValid = null,
                        status = DocumentValidationStatus.ERROR,
                        documentKind = document.documentKind,
                        summary = result.message,
                        issues = emptyList(),
                        legalStatus = DocumentLegalStatus.ERROR,
                        legalSummary = result.message
                    )
                    _uiState.update {
                        it.copy(isProcessing = false, message = result.message)
                    }
                }
            }
        }
    }

    private fun shareDocument(documentId: String) {
        val document = _uiState.value.documents.find { it.id == documentId }
            ?: _uiState.value.selectedDocument
        if (document == null) {
            showMessage("Document unavailable.")
            return
        }
        val uri = repository.shareUri(document)
        if (uri == null) {
            showMessage("The file is no longer on this device.")
            return
        }
        _uiState.update {
            it.copy(
                shareRequest = ScannerShareRequest(
                    uri = uri,
                    mimeType = document.mimeType,
                    name = document.name
                )
            )
        }
    }

    private fun deleteConfirmed() {
        val id = _uiState.value.confirmDeleteId ?: return
        viewModelScope.launch {
            repository.delete(id)
            _uiState.update {
                val backPage = if (it.openedFromHistory) ScannerPage.History else ScannerPage.Home
                it.copy(
                    confirmDeleteId = null,
                    selectedDocument = if (it.selectedDocument?.id == id) null else it.selectedDocument,
                    destination = if (it.selectedDocument?.id == id) backPage else it.destination,
                    message = "Document deleted."
                )
            }
        }
    }

    private fun copyExtractedText() {
        val text = _uiState.value.selectedDocument?.extractedText?.trim().orEmpty()
        if (text.isBlank()) {
            showMessage("No extracted text to copy.")
            return
        }
        val clipboard = getApplication<Application>()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Extracted text", text))
        showMessage("Text copied.")
    }

    private fun goBack() {
        _uiState.update { state ->
            val nextPage = when (state.destination) {
                ScannerPage.Detail -> if (state.openedFromHistory) {
                    ScannerPage.History
                } else {
                    ScannerPage.Home
                }
                ScannerPage.History -> ScannerPage.Home
                ScannerPage.Home -> ScannerPage.Home
            }
            state.copy(
                destination = nextPage,
                selectedDocument = if (nextPage == ScannerPage.Detail) state.selectedDocument else null
            )
        }
    }

    private fun selectTab(tab: ProfileTab) {
        if (tab == ProfileTab.Scanner) return
        if (tab == ProfileTab.Logout) {
            _uiState.update { it.copy(showLogoutDialog = true) }
            return
        }

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

    private fun confirmLogout() {
        _uiState.update { it.copy(showLogoutDialog = false) }
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(navigateTo = ScreenRoutes.Login.route) }
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    override fun onCleared() {
        repository.close()
        chatRepository.close()
        super.onCleared()
    }
}

private fun legalStatusFrom(
    result: com.example.assignment.database.remote.ChatBox.DocumentValidation
): DocumentLegalStatus {
    if (!result.valid) return DocumentLegalStatus.SKIPPED
    return runCatching { DocumentLegalStatus.valueOf(result.legalStatus) }
        .getOrDefault(
            if (result.legal == true) {
                DocumentLegalStatus.COMPLIANT
            } else {
                DocumentLegalStatus.NON_COMPLIANT
            }
        )
}

private fun greetingForHour(
    hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
): String = when (hour) {
    in 5..11 -> "Good morning! 👋"
    in 12..16 -> "Good afternoon! 👋"
    in 17..20 -> "Good evening! 👋"
    else -> "Good night! 👋"
}
