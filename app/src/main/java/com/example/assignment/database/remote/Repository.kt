package com.example.assignment.database.remote

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
    suspend fun loadChatConversations(): Result<List<ChatConversation>>
    suspend fun createChatConversation(title: String): Result<ChatConversation>
    suspend fun loadChatHistory(conversationId: String): Result<List<ChatHistoryMessage>>
    suspend fun saveChatMessage(
        conversationId: String,
        text: String,
        isFromUser: Boolean
    ): Result<Unit>
}