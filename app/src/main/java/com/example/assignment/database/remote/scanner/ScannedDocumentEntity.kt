package com.example.assignment.database.remote.scanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_documents")
data class ScannedDocumentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val source: String,
    val filePath: String,
    val thumbnailPath: String?,
    val mimeType: String,
    val extractedText: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val createdAt: Long,
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