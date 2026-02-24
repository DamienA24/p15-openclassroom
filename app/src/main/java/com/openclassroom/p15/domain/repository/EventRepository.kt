package com.openclassroom.p15.domain.repository

import android.net.Uri
import com.openclassroom.p15.domain.model.Event

interface EventRepository {
    suspend fun createEvent(event: Event): Result<String>
    suspend fun uploadImage(imageUri: Uri): Result<String>
    suspend fun getEvent(eventId: String): Result<Event?>
    suspend fun getAllEvents(): Result<List<Event>>
}
