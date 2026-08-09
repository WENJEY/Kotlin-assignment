package com.example.assignment.database

import com.example.assignment.ui.utils.Result

interface Repository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(username: String, email: String, password: String): Result<User>
    suspend fun getEmailByUsername(username: String): Result<String>
    fun currentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun logout()
}
