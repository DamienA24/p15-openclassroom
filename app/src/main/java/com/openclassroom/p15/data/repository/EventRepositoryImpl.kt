package com.openclassroom.p15.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.repository.EventRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : EventRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")
    private val storage = FirebaseStorage.getInstance()

    override suspend fun createEvent(event: Event): Result<String> {
        return try {
            val docRef = eventsCollection.add(event).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                ?: return Result.failure(Exception("Impossible d'ouvrir l'image"))
            val fileName = "events/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)
            ref.putBytes(bytes).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

}
