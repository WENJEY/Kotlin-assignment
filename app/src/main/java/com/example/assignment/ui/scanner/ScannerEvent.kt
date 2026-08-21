package com.example.assignment.ui.scanner

import android.net.Uri
import com.example.assignment.ui.profile.ProfileTab

sealed class ScannerEvent {
    data object ScreenOpened : ScannerEvent()
    data object ScanClicked : ScannerEvent()
    data object ImportClicked : ScannerEvent()
    data object HistoryClicked : ScannerEvent()
    data object ViewAllClicked : ScannerEvent()
    data class DocumentClicked(val documentId: String) : ScannerEvent()
    data class ShareClicked(val documentId: String) : ScannerEvent()
    data class DeleteClicked(val documentId: String) : ScannerEvent()
    data object DeleteConfirmed : ScannerEvent()
    data object DeleteCanceled : ScannerEvent()
    data object CopyTextClicked : ScannerEvent()
    data object RecheckClicked : ScannerEvent()
    data object BackClicked : ScannerEvent()
    data object LaunchActionHandled : ScannerEvent()
    data object DocumentScannerUnavailable : ScannerEvent()
    data class ScanCaptured(
        val imageUris: List<Uri>,
        val pdfUri: Uri?
    ) : ScannerEvent()
    data class CameraImageCaptured(val uri: Uri) : ScannerEvent()
    data class FileImported(val uri: Uri) : ScannerEvent()
    data object CameraPermissionDenied : ScannerEvent()
    data object ShareHandled : ScannerEvent()
    data class TabSelected(val tab: ProfileTab) : ScannerEvent()
    data object LogoutConfirmed : ScannerEvent()
    data object LogoutCanceled : ScannerEvent()
    data object NavigationHandled : ScannerEvent()
    data object MessageShown : ScannerEvent()
}
