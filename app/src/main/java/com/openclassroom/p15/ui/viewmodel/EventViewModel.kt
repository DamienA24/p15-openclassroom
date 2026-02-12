package com.openclassroom.p15.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p15.data.model.Event
import com.openclassroom.p15.data.repository.EventRepository
import com.openclassroom.p15.data.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val eventRepository = EventRepository()
    private val userRepository = UserRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>> = _filteredEvents.asStateFlow()

    private val _creatorAvatars = MutableStateFlow<Map<String, String>>(emptyMap())
    val creatorAvatars: StateFlow<Map<String, String>> = _creatorAvatars.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            eventRepository.getAllEvents()
                .onSuccess { events ->
                    _events.value = events
                    applyFiltersAndSort()
                    loadCreatorAvatars(events)
                }
                .onFailure { e ->
                    _error.value = e.message ?: "An error occurred"
                }

            _isLoading.value = false
        }
    }

    private fun loadCreatorAvatars(events: List<Event>) {
        val creatorIds = events.map { it.creatorId }.distinct().filter { it.isNotBlank() }
        viewModelScope.launch {
            val results = creatorIds.map { creatorId ->
                async {
                    userRepository.getUser(creatorId).getOrNull()?.let { user ->
                        if (user.avatarUrl.isNotBlank()) creatorId to user.avatarUrl else null
                    }
                }
            }.awaitAll()

            _creatorAvatars.value = results.filterNotNull().toMap()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFiltersAndSort()
    }

    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        var result = _events.value

        val query = _searchQuery.value
        if (query.isNotBlank()) {
            result = result.filter { it.title.contains(query, ignoreCase = true) }
        }

        result = if (_sortAscending.value) {
            result.sortedBy { it.date }
        } else {
            result.sortedByDescending { it.date }
        }

        _filteredEvents.value = result
    }
}
