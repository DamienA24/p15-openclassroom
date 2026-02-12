package com.openclassroom.p15.data.repository

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import com.openclassroom.p15.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signOut(context: Context): Result<Unit> {
        return try {
            AuthUI.getInstance().signOut(context).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
