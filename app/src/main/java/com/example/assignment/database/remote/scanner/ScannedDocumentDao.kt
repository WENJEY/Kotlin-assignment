package com.example.assignment.database.remote.scanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedDocumentDao {
    @Query("SELECT * FROM scanned_documents ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ScannedDocumentEntity>>

    @Query("SELECT * FROM scanned_documents WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ScannedDocumentEntity?

    @Query("SELECT * FROM scanned_documents ORDER BY createdAt DESC")
    suspend fun getAll(): List<ScannedDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: ScannedDocumentEntity)

    @Query("DELETE FROM scanned_documents WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query(
        """
        UPDATE scanned_documents
        SET isValid = :isValid,
            validationStatus = :validationStatus,
            documentKind = :documentKind,
            validationSummary = :validationSummary,
            validationIssues = :validationIssues,
            isLegal = :isLegal,
            legalStatus = :legalStatus,
            legalStatute = :legalStatute,
            legalSummary = :legalSummary,
            legalViolations = :legalViolations,
            legalMissing = :legalMissing,
            legalNextSteps = :legalNextSteps
        WHERE id = :id
        """
    )
    suspend fun updateValidation(
        id: String,
        isValid: Boolean?,
        validationStatus: String,
        documentKind: String,
        validationSummary: String,
        validationIssues: String,
        isLegal: Boolean?,
        legalStatus: String,
        legalStatute: String,
        legalSummary: String,
        legalViolations: String,
        legalMissing: String,
        legalNextSteps: String
    )
}