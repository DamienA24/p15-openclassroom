package com.openclassroom.p15.domain.repository

import com.openclassroom.p15.domain.model.Event

interface EventRepository {
    suspend fun createEvent(event: Event): Result<String>
    suspend fun getEvent(eventId: String): Result<Event?>
    suspend fun getAllEvents(): Result<List<Event>>
    suspend fun getEventsByCreator(creatorId: String): Result<List<Event>>
    suspend fun updateEvent(eventId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun getUpcomingEvents(): Result<List<Event>>
    suspend fun getPastEvents(): Result<List<Event>>
}
