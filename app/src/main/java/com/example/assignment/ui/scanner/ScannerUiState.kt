package com.example.assignment.ui.scanner

import android.net.Uri
import com.example.assignment.ui.profile.ProfileTab

enum class DocumentType {
    PDF,
    IMAGE
}

enum class DocumentSource {
    SCAN,
    IMPORT
}

enum class ScannerPage {
    Home,
    History,
    Detail
}

enum class ScannerLaunchAction {
    None,
    DocumentScanner,
    Camera,
    Import
}

enum class DocumentValidationStatus {
    NONE,
    CHECKING,
    VALID,
    INVALID,
    UNREADABLE,
    ERROR
}

fun DocumentValidationStatus.label(): String = when (this) {
    DocumentValidationStatus.NONE -> "Not checked"
    DocumentValidationStatus.CHECKING -> "Checking..."
    DocumentValidationStatus.VALID -> "Valid"
    DocumentValidationStatus.INVALID -> "Invalid"
    DocumentValidationStatus.UNREADABLE -> "Unreadable"
    DocumentValidationStatus.ERROR -> "Check failed"
}

enum class DocumentLegalStatus {
    NONE,
    CHECKING,
    COMPLIANT,
    NON_COMPLIANT,
    SKIPPED,
    ERROR
}

fun DocumentLegalStatus.label(): String = when (this) {
    DocumentLegalStatus.NONE -> "Labour law not checked"
    DocumentLegalStatus.CHECKING -> "Checking labour law..."
    DocumentLegalStatus.COMPLIANT -> "Meets labour law"
    DocumentLegalStatus.NON_COMPLIANT -> "Does not meet labour law"
    DocumentLegalStatus.SKIPPED -> "Labour law not checked"
    DocumentLegalStatus.ERROR -> "Labour-law check failed"
}

data class ScannedDocument(
    val id: String,
    val name: String,
    val metadata: String,
    val type: DocumentType,
    val source: DocumentSource,
    val filePath: String,
    val thumbnailPath: String?,
    val mimeType: String,
    val extractedText: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val createdAt: Long,
    val isValid: Boolean? = null,
    val validationStatus: DocumentValidationStatus = DocumentValidationStatus.NONE,
    val documentKind: String = "",
    val validationSummary: String = "",
    val validationIssues: List<String> = emptyList(),
    val isLegal: Boolean? = null,
    val legalStatus: DocumentLegalStatus = DocumentLegalStatus.NONE,
    val legalStatute: String = "",
    val legalSummary: String = "",
    val legalViolations: List<String> = emptyList(),
    val legalMissing: List<String> = emptyList(),
    val legalNextSteps: List<String> = emptyList()
)

data class ScannerShareRequest(
    val uri: Uri,
    val mimeType: String,
    val name: String
)

data class ScannerUiState(
    val greeting: String = "Good morning! 👋",
    val subtitle: String = "Let's scan something today.",
    val documents: List<ScannedDocument> = emptyList(),
    val selectedDocument: ScannedDocument? = null,
    val destination: ScannerPage = ScannerPage.Home,
    val openedFromHistory: Boolean = false,
    val launchAction: ScannerLaunchAction = ScannerLaunchAction.None,
    val shareRequest: ScannerShareRequest? = null,
    val confirmDeleteId: String? = null,
    val isProcessing: Boolean = false,
    val processingMessage: String = "Reading document...",
    val selectedTab: ProfileTab = ProfileTab.Scanner,
    val showLogoutDialog: Boolean = false,
    val navigateTo: String? = null,
    val message: String? = null
) {
    val recentDocuments: List<ScannedDocument>
        get() = documents.take(5)
}
