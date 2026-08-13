package com.example.assignment.database

import com.example.assignment.ui.utils.Result

interface Repository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(username: String, email: String, password: String): Result<User>
    suspend fun getEmailByUsername(username: String): Result<String>
    fun currentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun logout()
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun uploadProfileImage(userId: String, imageBytes: ByteArray): Result<String>
    suspend fun updateProfileImage(imageUrl: String): Result<Unit>
    suspend fun getProfileImageUrl(): Result<String?>
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUserProfile(
        username: String,
        age: Int?,
        phoneNumber: String,
        gender: String
    ): Result<Unit>
    suspend fun submitFeedback(feedback: Feedback): Result<Unit>
}
