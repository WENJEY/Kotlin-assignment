package com.example.assignment.database

import android.util.Log
import com.example.assignment.ui.utils.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository : Repository {


    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override fun logout() = auth.signOut()

    // NEW: Get email by username from Firestore
    override suspend fun getEmailByUsername(username: String): String? {
        return try {
            val query = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            query.documents.firstOrNull()?.getString("email")
        } catch (e: Exception) {
            null
        }
    }

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
            Result.Error(mapFirebaseError(e))
        }
    }

    override suspend fun signUp(username: String, email: String, password: String): Result<User> {
        return try {
            // 1. Create Firebase Auth user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.Error("Registration failed")

            // 2. Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. Save to Firestore with separate error handling
            val userData = hashMapOf(
                "username" to username,
                "email" to email,
                "uid" to firebaseUser.uid,
                "createdAt" to Timestamp.now()
            )

            try {
                db.collection("users").document(firebaseUser.uid)
                    .set(userData)
                    .await()
            } catch (e: Exception) {
                // Log but don't fail — user can still login
                Log.e("FirebaseRepository", "Firestore save failed: ${e.message}")
                // Optionally retry or report to analytics
            }

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

    // FIXED: Use 'e' parameter
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