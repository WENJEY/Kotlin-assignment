package com.example.assignment.ui.database

import com.example.assignment.ui.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseRepository : Repository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return Result.Error("Login failed")

            Result.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    username = firebaseUser.displayName ?: email.substringBefore("@")
                )
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() = auth.signOut()

    override suspend fun signUp(username: String, email: String, password: String): Result<User> {
        return try {
            // 1. 创建 Firebase 用户
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return Result.Error("Registration failed")

            // 2. 更新显示名（用户名）
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. 返回用户数据
            Result.Success(
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    username = username
                )
            )
        } catch (e: Exception) {
            Result.Error(mapFirebaseError(e))
        }
    }

    private fun mapFirebaseError(e: Exception): String {
        val msg = e.message ?: "Unknown error"
        return when {
            msg.contains("badly formatted", ignoreCase = true) -> "Invalid email format"
            msg.contains("password is invalid", ignoreCase = true) -> "Wrong password"
            msg.contains("no user record", ignoreCase = true) -> "Account not found"
            msg.contains("already in use", ignoreCase = true) -> "Email already registered"
            msg.contains("weak password", ignoreCase = true) -> "Password too weak (min 6 chars)"
            else -> msg
        }
    }
}