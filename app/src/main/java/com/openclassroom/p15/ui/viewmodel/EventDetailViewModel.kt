package com.openclassroom.p15.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _creatorAvatarUrl = MutableStateFlow<String?>(null)
    val creatorAvatarUrl: StateFlow<String?> = _creatorAvatarUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    if (event != null) {
                        _event.value = event
                        if (event.creatorId.isNotBlank()) {
                            userRepository.getUser(event.creatorId).getOrNull()?.let { user ->
                                _creatorAvatarUrl.value = user.avatarUrl.ifBlank { null }
                            }
                        }
                    } else {
                        _error.value = "Event not found"
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "An error occurred"
                }

            _isLoading.value = false
        }
    }
}
