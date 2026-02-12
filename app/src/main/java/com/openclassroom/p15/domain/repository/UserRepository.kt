package com.openclassroom.p15.domain.repository

import com.openclassroom.p15.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User?>
    suspend fun updateUser(uid: String, updates: Map<String, Any>): Result<Unit>
    suspend fun updateNotificationPreference(uid: String, enabled: Boolean): Result<Unit>
    suspend fun deleteUser(uid: String): Result<Unit>
}
