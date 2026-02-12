package com.openclassroom.p15.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p15.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    fun signOut(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut(context)
            onComplete()
        }
    }
}
