package com.example.assignment.database.remote.Data

data class RemoteScannedDocument(
    val id: String,
    val name: String,
    val type: String,
    val source: String,
    val mimeType: String,
    val extractedText: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val createdAt: Long,
    val storageFilePath: String = "",
    val storageThumbnailPath: String? = null,
    val isValid: Boolean? = null,
    val validationStatus: String = "NONE",
    val documentKind: String = "",
    val validationSummary: String = "",
    val validationIssues: String = "",
    val isLegal: Boolean? = null,
    val legalStatus: String = "NONE",
    val legalStatute: String = "",
    val legalSummary: String = "",
    val legalViolations: String = "",
    val legalMissing: String = "",
    val legalNextSteps: String = ""
)
