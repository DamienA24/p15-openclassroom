package com.openclassroom.p15.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUser() {
        val uid = authRepository.currentUser?.uid ?: return
        loadUserById(uid)
    }

    fun loadUserById(uid: String) {
        if (uid.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.getUser(uid)
                .onSuccess { user ->
                    if (user != null) {
                        _user.value = user
                    } else {
                        val firebaseUser = authRepository.currentUser
                        val newUser = User(
                            uid = uid,
                            email = firebaseUser?.email ?: "",
                            firstName = firebaseUser?.displayName?.split(" ")?.firstOrNull() ?: "",
                            lastName = firebaseUser?.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                            avatarUrl = firebaseUser?.photoUrl?.toString() ?: ""
                        )
                        userRepository.createUser(newUser)
                        _user.value = newUser
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "An error occurred"
                }

            _isLoading.value = false
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val uid = authRepository.currentUser?.uid ?: return
        _user.value = _user.value?.copy(notificationsEnabled = enabled)
        viewModelScope.launch {
            userRepository.updateNotificationPreference(uid, enabled)
        }
    }
}
