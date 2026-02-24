package com.openclassroom.p15.ui.viewmodel

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.EventLocation
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var address by mutableStateOf("")

    var selectedDateMillis by mutableStateOf<Long?>(null)

    val cal: Calendar = Calendar.getInstance()
    var selectedHour by mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY))
    var selectedMinute by mutableIntStateOf(cal.get(Calendar.MINUTE))

    var imageUri by mutableStateOf<Uri?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _eventCreated = MutableStateFlow(false)
    val eventCreated: StateFlow<Boolean> = _eventCreated.asStateFlow()

    fun createEvent(context: Context) {
        if (title.isBlank()) {
            _error.value = "Le titre est obligatoire"
            return
        }
        if (selectedDateMillis == null) {
            _error.value = "La date est obligatoire"
            return
        }
        if (address.isBlank()) {
            _error.value = "L'adresse est obligatoire"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val imageUrl = if (imageUri != null) {
                    eventRepository.uploadImage(imageUri!!).getOrElse { e ->
                        _error.value = "Échec de l'upload de l'image : ${e.message}"
                        _isLoading.value = false
                        return@launch
                    }
                } else ""

                val location = withContext(Dispatchers.IO) {
                    geocodeAddress(context, address)
                }

                val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utcCal.timeInMillis = selectedDateMillis!!
                val localCal = Calendar.getInstance()
                localCal.set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                localCal.set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                localCal.set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                localCal.set(Calendar.HOUR_OF_DAY, selectedHour)
                localCal.set(Calendar.MINUTE, selectedMinute)
                localCal.set(Calendar.SECOND, 0)
                localCal.set(Calendar.MILLISECOND, 0)
                val timestamp = Timestamp(Date(localCal.timeInMillis))

                val currentUser = authRepository.currentUser ?: run {
                    _error.value = "Utilisateur non connecté"
                    _isLoading.value = false
                    return@launch
                }
                val user = userRepository.getUser(currentUser.uid).getOrNull()
                val creatorName = when {
                    user != null && (user.firstName + user.lastName).isNotBlank() ->
                        "${user.firstName} ${user.lastName}".trim()
                    currentUser.displayName != null -> currentUser.displayName!!
                    else -> currentUser.email ?: ""
                }

                val event = Event(
                    title = title,
                    description = description,
                    date = timestamp,
                    location = location,
                    imageUrl = imageUrl,
                    creatorId = currentUser.uid,
                    creatorName = creatorName
                )
                eventRepository.createEvent(event)
                    .onSuccess { _eventCreated.value = true }
                    .onFailure { _error.value = it.message }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun geocodeAddress(context: Context, addressStr: String): EventLocation {
        return try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(addressStr, 1)
            if (!addresses.isNullOrEmpty()) {
                EventLocation(
                    address = addressStr,
                    latitude = addresses[0].latitude,
                    longitude = addresses[0].longitude
                )
            } else {
                EventLocation(address = addressStr)
            }
        } catch (e: Exception) {
            EventLocation(address = addressStr)
        }
    }
}
