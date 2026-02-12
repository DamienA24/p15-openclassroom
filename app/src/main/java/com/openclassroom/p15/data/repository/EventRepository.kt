package com.openclassroom.p15.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassroom.p15.data.model.Event
import kotlinx.coroutines.tasks.await

class EventRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")

    suspend fun createEvent(event: Event): Result<String> {
        return TODO("Provide the return value")
    }

    suspend fun getEvent(eventId: String): Result<Event?> {
        return try {
            val snapshot = eventsCollection.document(eventId).get().await()
            val event = snapshot.toObject(Event::class.java)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection.get().await()
            val events = snapshot.toObjects(Event::class.java)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventsByCreator(creatorId: String): Result<List<Event>> {
        return TODO("Provide the return value")
    }

    suspend fun updateEvent(eventId: String, updates: Map<String, Any>): Result<Unit> {
        return TODO("Provide the return value")
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return TODO("Provide the return value")
    }

    suspend fun getUpcomingEvents(): Result<List<Event>> {
        return TODO("Provide the return value")
    }

    suspend fun getPastEvents(): Result<List<Event>> {
        return TODO("Provide the return value")
    }
}
