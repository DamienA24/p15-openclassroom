package com.openclassroom.p15.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassroom.p15.data.model.User
import kotlinx.coroutines.tasks.await

/**
 * Repository for User operations with Firestore
 */
class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /**
     * Create a new user in Firestore
     */
    suspend fun createUser(user: User): Result<Unit> {
        return TODO("Provide the return value")
    }

    /**
     * Get a user by UID
     */
    suspend fun getUser(uid: String): Result<User?> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user profile
     */
    suspend fun updateUser(uid: String, updates: Map<String, Any>): Result<Unit> {
        return TODO("Provide the return value")
    }

    /**
     * Update notification preference
     */
    suspend fun updateNotificationPreference(uid: String, enabled: Boolean): Result<Unit> {
        return TODO("Provide the return value")
    }

    /**
     * Delete user
     */
    suspend fun deleteUser(uid: String): Result<Unit> {
        return TODO("Provide the return value")
    }
}
