package com.openclassroom.p15.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    override suspend fun createUser(user: User): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun getUser(uid: String): Result<User?> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(uid: String, updates: Map<String, Any>): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun updateNotificationPreference(uid: String, enabled: Boolean): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun deleteUser(uid: String): Result<Unit> {
        return TODO("Provide the return value")
    }
}
