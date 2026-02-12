package com.openclassroom.p15.domain.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signOut(context: Context): Result<Unit>
}
