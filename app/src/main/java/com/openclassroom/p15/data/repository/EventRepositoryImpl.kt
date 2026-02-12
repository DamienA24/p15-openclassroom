package com.openclassroom.p15.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.repository.EventRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor() : EventRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")

    override suspend fun createEvent(event: Event): Result<String> {
        return TODO("Provide the return value")
    }

    override suspend fun getEvent(eventId: String): Result<Event?> {
        return try {
            val snapshot = eventsCollection.document(eventId).get().await()
            val event = snapshot.toObject(Event::class.java)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection.get().await()
            val events = snapshot.toObjects(Event::class.java)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEventsByCreator(creatorId: String): Result<List<Event>> {
        return TODO("Provide the return value")
    }

    override suspend fun updateEvent(eventId: String, updates: Map<String, Any>): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return TODO("Provide the return value")
    }

    override suspend fun getUpcomingEvents(): Result<List<Event>> {
        return TODO("Provide the return value")
    }

    override suspend fun getPastEvents(): Result<List<Event>> {
        return TODO("Provide the return value")
    }
}
