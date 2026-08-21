package com.example.assignment.database.remote.scanner

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import com.example.assignment.database.local.AppDatabase
import com.example.assignment.database.remote.Data.RemoteScannedDocument
import com.example.assignment.database.remote.Repository
import com.example.assignment.database.remote.supabase.SupabaseRepository
import com.example.assignment.ui.scanner.DocumentLegalStatus
import com.example.assignment.ui.scanner.DocumentSource
import com.example.assignment.ui.scanner.DocumentType
import com.example.assignment.ui.scanner.DocumentValidationStatus
import com.example.assignment.ui.scanner.ScannedDocument
import com.example.assignment.ui.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class ScannerRepository(
    context: Context,
    private val dao: ScannedDocumentDao = AppDatabase.getInstance(context).scannedDocumentDao(),
    private val remote: Repository = SupabaseRepository()
) {
    private val appContext = context.applicationContext
    private val ocr = DocumentOcr()

    fun observeDocuments(): Flow<List<ScannedDocument>> =
        dao.observeAll().map { rows -> rows.map { it.toUi() } }

    suspend fun getDocument(id: String): ScannedDocument? =
        dao.getById(id)?.toUi()

    suspend fun saveScan(
        imageUris: List<Uri>,
        pdfUri: Uri?,
        source: DocumentSource,
        displayName: String? = null
    ): Result<ScannedDocument> = withContext(Dispatchers.IO) {
        runCatching {
            require(imageUris.isNotEmpty() || pdfUri != null) { "No document was captured." }

            val id = UUID.randomUUID().toString()
            val folder = File(appContext.filesDir, "scans/$id").apply { mkdirs() }
            val copiedImages = imageUris.mapIndexed { index, uri ->
                copyUri(uri, File(folder, "page_$index.jpg"))
            }
            val copiedPdf = pdfUri?.let { copyUri(it, File(folder, "document.pdf")) }

            val type = if (copiedPdf != null || isPdf(displayName, pdfUri)) {
                DocumentType.PDF
            } else {
                DocumentType.IMAGE
            }
            val mainFile = copiedPdf ?: copiedImages.first()
            val thumbnail = copiedImages.firstOrNull()
                ?: renderPdfThumbnail(mainFile, File(folder, "thumbnail.jpg"))
            val extractedText = extractText(
                imageFiles = copiedImages,
                pdfFile = copiedPdf ?: mainFile.takeIf { type == DocumentType.PDF }
            )
            val pageCount = when {
                copiedImages.isNotEmpty() -> copiedImages.size
                type == DocumentType.PDF -> pdfPageCount(mainFile)
                else -> 1
            }
            val name = displayName?.takeIf { it.isNotBlank() }
                ?: defaultName(source, type)
            val entity = ScannedDocumentEntity(
                id = id,
                name = name,
                type = type.name,
                source = source.name,
                filePath = mainFile.absolutePath,
                thumbnailPath = thumbnail?.absolutePath,
                mimeType = if (type == DocumentType.PDF) "application/pdf" else "image/jpeg",
                extractedText = extractedText,
                fileSizeBytes = mainFile.length(),
                pageCount = pageCount.coerceAtLeast(1),
                createdAt = System.currentTimeMillis(),
                validationStatus = "CHECKING"
            )
            dao.insert(entity)
            pushToCloud(entity, includeFiles = true)
            entity.toUi()
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(it.message ?: "Could not save the document.") }
        )
    }

    suspend fun saveImportedUri(uri: Uri, source: DocumentSource): Result<ScannedDocument> {
        takePersistableRead(uri)
        val name = queryDisplayName(uri)
        val mime = appContext.contentResolver.getType(uri).orEmpty()
        return if (mime.contains("pdf", ignoreCase = true) || name.endsWith(".pdf", true)) {
            saveScan(imageUris = emptyList(), pdfUri = uri, source = source, displayName = name)
        } else {
            saveScan(imageUris = listOf(uri), pdfUri = null, source = source, displayName = name)
        }
    }

    suspend fun saveValidation(
        id: String,
        isValid: Boolean?,
        status: DocumentValidationStatus,
        documentKind: String,
        summary: String,
        issues: List<String>,
        isLegal: Boolean? = null,
        legalStatus: DocumentLegalStatus = DocumentLegalStatus.NONE,
        legalStatute: String = "",
        legalSummary: String = "",
        violations: List<String> = emptyList(),
        missingRequirements: List<String> = emptyList(),
        nextSteps: List<String> = emptyList()
    ) = withContext(Dispatchers.IO) {
        dao.updateValidation(
            id = id,
            isValid = isValid,
            validationStatus = status.name,
            documentKind = documentKind,
            validationSummary = summary,
            validationIssues = issues.joinToString("\n"),
            isLegal = isLegal,
            legalStatus = legalStatus.name,
            legalStatute = legalStatute,
            legalSummary = legalSummary,
            legalViolations = violations.joinToString("\n"),
            legalMissing = missingRequirements.joinToString("\n"),
            legalNextSteps = nextSteps.joinToString("\n")
        )
        dao.getById(id)?.let { pushToCloud(it, includeFiles = false) }
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        File(appContext.filesDir, "scans/$id").deleteRecursively()
        dao.deleteById(id)
        if (remote.isLoggedIn()) {
            when (val result = remote.deleteScannedDocument(id)) {
                is Result.Error -> Log.e(Tag, result.message)
                is Result.Success -> Unit
            }
        }
    }

    suspend fun syncWithCloud(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!remote.isLoggedIn()) return@withContext Result.Success(Unit)

        val localRows = dao.getAll()
        val remoteRows = when (val result = remote.loadScannedDocuments()) {
            is Result.Success -> result.data
            is Result.Error -> return@withContext result
        }
        val remoteIds = remoteRows.map { it.id }.toSet()

        localRows
            .filter { it.id !in remoteIds }
            .forEach { pushToCloud(it, includeFiles = true) }

        val mergedRemote = when (val result = remote.loadScannedDocuments()) {
            is Result.Success -> result.data
            is Result.Error -> remoteRows
        }
        mergedRemote.forEach { materializeRemote(it) }
        Result.Success(Unit)
    }

    fun shareUri(document: ScannedDocument): Uri? {
        val file = File(document.filePath)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
    }

    fun close() {
        ocr.close()
    }

    private suspend fun pushToCloud(entity: ScannedDocumentEntity, includeFiles: Boolean) {
        if (!remote.isLoggedIn()) return
        val fileBytes = if (includeFiles) File(entity.filePath).takeIf { it.exists() }?.readBytes() else null
        val thumbnailBytes = if (includeFiles) {
            entity.thumbnailPath?.let { File(it).takeIf { file -> file.exists() }?.readBytes() }
        } else {
            null
        }
        when (val result = remote.upsertScannedDocument(entity.toRemote(), fileBytes, thumbnailBytes)) {
            is Result.Error -> Log.e(Tag, result.message)
            is Result.Success -> Unit
        }
    }

    private suspend fun materializeRemote(record: RemoteScannedDocument) {
        val folder = File(appContext.filesDir, "scans/${record.id}").apply { mkdirs() }
        val existing = dao.getById(record.id)
        val existingFile = existing?.filePath?.let { File(it) }?.takeIf { it.exists() }
        val existingThumb = existing?.thumbnailPath?.let { File(it) }?.takeIf { it.exists() }

        val mainFile = existingFile ?: downloadStoredFile(
            storagePath = record.storageFilePath,
            destination = File(folder, localFileName(record))
        )
        val thumbnail = existingThumb ?: record.storageThumbnailPath?.let { path ->
            downloadStoredFile(path, File(folder, "thumbnail.jpg"))
        }

        dao.insert(
            record.toEntity(
                filePath = mainFile?.absolutePath
                    ?: existing?.filePath
                    ?: File(folder, localFileName(record)).absolutePath,
                thumbnailPath = thumbnail?.absolutePath ?: existing?.thumbnailPath
            )
        )
    }

    private suspend fun downloadStoredFile(storagePath: String, destination: File): File? {
        if (storagePath.isBlank()) return null
        return when (val result = remote.downloadScanFile(storagePath)) {
            is Result.Success -> {
                destination.parentFile?.mkdirs()
                destination.writeBytes(result.data)
                destination
            }
            is Result.Error -> {
                Log.e(Tag, result.message)
                null
            }
        }
    }

    private fun takePersistableRead(uri: Uri) {
        runCatching {
            appContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun copyUri(uri: Uri, destination: File): File {
        appContext.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: error("Unable to read the selected file.")
        return destination
    }

    private suspend fun extractText(imageFiles: List<File>, pdfFile: File?): String {
        val fromImages = imageFiles.take(MaxOcrPages).mapIndexed { index, file ->
            val pageText = ocr.fromUri(appContext, Uri.fromFile(file)).trim()
            if (imageFiles.size > 1 && pageText.isNotBlank()) {
                "Page ${index + 1}\n$pageText"
            } else {
                pageText
            }
        }.filter { it.isNotBlank() }

        if (fromImages.isNotEmpty()) {
            return fromImages.joinToString("\n\n")
        }

        if (pdfFile == null || !pdfFile.exists()) return ""

        return ocrPdf(pdfFile)
    }

    private suspend fun ocrPdf(pdfFile: File): String {
        return PdfRenderer(
            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        ).use { renderer ->
            buildString {
                val pages = minOf(renderer.pageCount, MaxOcrPages)
                for (index in 0 until pages) {
                    val page = renderer.openPage(index)
                    val bitmap = Bitmap.createBitmap(
                        (page.width * 2).coerceAtLeast(1),
                        (page.height * 2).coerceAtLeast(1),
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    val text = ocr.fromBitmap(bitmap).trim()
                    bitmap.recycle()
                    if (text.isNotBlank()) {
                        if (isNotEmpty()) append("\n\n")
                        if (pages > 1) append("Page ${index + 1}\n")
                        append(text)
                    }
                }
            }
        }
    }

    private fun renderPdfThumbnail(pdfFile: File, destination: File): File? {
        if (!pdfFile.exists()) return null
        return runCatching {
            PdfRenderer(
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            ).use { renderer ->
                if (renderer.pageCount == 0) return@runCatching null
                val page = renderer.openPage(0)
                val bitmap = Bitmap.createBitmap(
                    page.width.coerceAtLeast(1),
                    page.height.coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                FileOutputStream(destination).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                }
                bitmap.recycle()
                destination
            }
        }.getOrNull()
    }

    private fun pdfPageCount(pdfFile: File): Int {
        if (!pdfFile.exists()) return 1
        return runCatching {
            PdfRenderer(
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            ).use { it.pageCount }
        }.getOrDefault(1)
    }

    private fun queryDisplayName(uri: Uri): String {
        val fallback = uri.lastPathSegment?.substringAfterLast('/') ?: "Imported document"
        return runCatching {
            appContext.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && index >= 0) cursor.getString(index) else null
                }
        }.getOrNull()?.takeIf { !it.isNullOrBlank() } ?: fallback
    }

    private fun isPdf(displayName: String?, uri: Uri?): Boolean {
        val mime = uri?.let { appContext.contentResolver.getType(it) }.orEmpty()
        return mime.contains("pdf", ignoreCase = true) ||
            displayName?.endsWith(".pdf", ignoreCase = true) == true
    }

    private fun defaultName(source: DocumentSource, type: DocumentType): String {
        val stamp = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.getDefault())
            .format(Instant.now().atZone(ZoneId.systemDefault()))
        val prefix = if (source == DocumentSource.SCAN) "Scan" else "Import"
        val suffix = if (type == DocumentType.PDF) "pdf" else "jpg"
        return "$prefix $stamp.$suffix"
    }

    companion object {
        private const val MaxOcrPages = 8
        private const val Tag = "ScannerRepository"
    }
}

private fun ScannedDocumentEntity.toRemote() = RemoteScannedDocument(
    id = id,
    name = name,
    type = type,
    source = source,
    mimeType = mimeType,
    extractedText = extractedText,
    fileSizeBytes = fileSizeBytes,
    pageCount = pageCount,
    createdAt = createdAt,
    isValid = isValid,
    validationStatus = validationStatus,
    documentKind = documentKind,
    validationSummary = validationSummary,
    validationIssues = validationIssues,
    isLegal = isLegal,
    legalStatus = legalStatus,
    legalStatute = legalStatute,
    legalSummary = legalSummary,
    legalViolations = legalViolations,
    legalMissing = legalMissing,
    legalNextSteps = legalNextSteps
)

private fun RemoteScannedDocument.toEntity(
    filePath: String,
    thumbnailPath: String?
) = ScannedDocumentEntity(
    id = id,
    name = name,
    type = type,
    source = source,
    filePath = filePath,
    thumbnailPath = thumbnailPath,
    mimeType = mimeType,
    extractedText = extractedText,
    fileSizeBytes = fileSizeBytes,
    pageCount = pageCount,
    createdAt = createdAt,
    isValid = isValid,
    validationStatus = validationStatus,
    documentKind = documentKind,
    validationSummary = validationSummary,
    validationIssues = validationIssues,
    isLegal = isLegal,
    legalStatus = legalStatus,
    legalStatute = legalStatute,
    legalSummary = legalSummary,
    legalViolations = legalViolations,
    legalMissing = legalMissing,
    legalNextSteps = legalNextSteps
)

private fun localFileName(record: RemoteScannedDocument): String =
    if (record.mimeType.contains("pdf", ignoreCase = true) || record.type.equals("PDF", true)) {
        "document.pdf"
    } else {
        "page_0.jpg"
    }

private fun ScannedDocumentEntity.toUi(): ScannedDocument {
    val type = runCatching { DocumentType.valueOf(type) }.getOrDefault(DocumentType.IMAGE)
    val source = runCatching { DocumentSource.valueOf(source) }.getOrDefault(DocumentSource.IMPORT)
    return ScannedDocument(
        id = id,
        name = name,
        metadata = formatMetadata(createdAt, fileSizeBytes, pageCount),
        type = type,
        source = source,
        filePath = filePath,
        thumbnailPath = thumbnailPath,
        mimeType = mimeType,
        extractedText = extractedText,
        fileSizeBytes = fileSizeBytes,
        pageCount = pageCount,
        createdAt = createdAt,
        isValid = isValid,
        validationStatus = runCatching {
            DocumentValidationStatus.valueOf(validationStatus)
        }.getOrDefault(DocumentValidationStatus.NONE),
        documentKind = documentKind,
        validationSummary = validationSummary,
        validationIssues = validationIssues
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() },
        isLegal = isLegal,
        legalStatus = runCatching {
            DocumentLegalStatus.valueOf(legalStatus)
        }.getOrDefault(DocumentLegalStatus.NONE),
        legalStatute = legalStatute,
        legalSummary = legalSummary,
        legalViolations = legalViolations
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() },
        legalMissing = legalMissing
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() },
        legalNextSteps = legalNextSteps
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    )
}

private fun formatMetadata(createdAt: Long, fileSizeBytes: Long, pageCount: Int): String {
    val zoned = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault())
    val today = LocalDate.now()
    val date = zoned.toLocalDate()
    val time = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()).format(zoned)
    val whenLabel = when (date) {
        today -> "Today, $time"
        today.minusDays(1) -> "Yesterday, $time"
        else -> DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()).format(zoned)
    }
    val pages = if (pageCount > 1) "  •  $pageCount pages" else ""
    return "$whenLabel  •  ${formatFileSize(fileSizeBytes)}$pages"
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format(Locale.getDefault(), "%.0f KB", kb)
    return String.format(Locale.getDefault(), "%.1f MB", kb / 1024.0)
}
