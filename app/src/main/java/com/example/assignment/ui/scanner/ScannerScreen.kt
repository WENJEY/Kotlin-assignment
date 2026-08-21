package com.example.assignment.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment.navigation.ScreenRoutes
import com.example.assignment.navigation.navigateToLoginAndClear
import com.example.assignment.ui.profile.LogoutConfirmDialog
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File

@Composable
fun ScannerScreen(
    navController: NavController,
    windowSize: WindowWidthSizeClass,
    viewModel: ScannerViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var cameraOutputUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        val uri = cameraOutputUri
        if (saved && uri != null) {
            viewModel.onEvent(ScannerEvent.CameraImageCaptured(uri))
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createCameraImageUri(context)
            cameraOutputUri = uri
            cameraLauncher.launch(uri)
        } else {
            viewModel.onEvent(ScannerEvent.CameraPermissionDenied)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onEvent(ScannerEvent.FileImported(it)) }
    }

    val documentScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
        val imageUris = scanResult?.pages?.map { it.imageUri }.orEmpty()
        val pdfUri = scanResult?.pdf?.uri
        if (imageUris.isNotEmpty() || pdfUri != null) {
            viewModel.onEvent(ScannerEvent.ScanCaptured(imageUris, pdfUri))
        }
    }

    fun startCameraCapture() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            val uri = createCameraImageUri(context)
            cameraOutputUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun startDocumentScanner() {
        val hostActivity = activity ?: return
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(false)
            .setPageLimit(8)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()

        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(hostActivity)
            .addOnSuccessListener { intentSender ->
                documentScannerLauncher.launch(
                    IntentSenderRequest.Builder(intentSender).build()
                )
            }
            .addOnFailureListener {
                viewModel.onEvent(ScannerEvent.DocumentScannerUnavailable)
            }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ScannerEvent.ScreenOpened)
    }

    LaunchedEffect(uiState.launchAction) {
        when (uiState.launchAction) {
            ScannerLaunchAction.DocumentScanner -> {
                viewModel.onEvent(ScannerEvent.LaunchActionHandled)
                startDocumentScanner()
            }
            ScannerLaunchAction.Camera -> {
                viewModel.onEvent(ScannerEvent.LaunchActionHandled)
                startCameraCapture()
            }
            ScannerLaunchAction.Import -> {
                viewModel.onEvent(ScannerEvent.LaunchActionHandled)
                importLauncher.launch(arrayOf("image/*", "application/pdf"))
            }
            ScannerLaunchAction.None -> Unit
        }
    }

    LaunchedEffect(uiState.shareRequest) {
        uiState.shareRequest?.let { request ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = request.mimeType
                putExtra(Intent.EXTRA_STREAM, request.uri)
                putExtra(Intent.EXTRA_SUBJECT, request.name)
                clipData = android.content.ClipData.newRawUri(request.name, request.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share document"))
            viewModel.onEvent(ScannerEvent.ShareHandled)
        }
    }

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { route ->
            if (route == ScreenRoutes.Login.route) {
                navController.navigateToLoginAndClear()
            } else {
                val currentRoute = navController.currentDestination?.route
                navController.navigate(route) {
                    launchSingleTop = true
                    if (currentRoute != null) {
                        popUpTo(currentRoute) { inclusive = true }
                    }
                }
            }
            viewModel.onEvent(ScannerEvent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ScannerEvent.MessageShown)
        }
    }

    ScannerLayout(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )

    if (uiState.showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = { viewModel.onEvent(ScannerEvent.LogoutConfirmed) },
            onDismiss = { viewModel.onEvent(ScannerEvent.LogoutCanceled) }
        )
    }
}

private fun createCameraImageUri(context: android.content.Context): Uri {
    val file = File(context.cacheDir, "scan_capture_${System.currentTimeMillis()}.jpg")
    file.parentFile?.mkdirs()
    if (!file.exists()) {
        file.createNewFile()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
