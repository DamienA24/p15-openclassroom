package com.openclassroom.p15.testutil

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.EventLocation
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository

class FakeEventRepository(
    private val events: List<Event> = emptyList(),
    private val singleEvent: Event? = null,
    private val shouldFail: Boolean = false,
    private val uploadShouldFail: Boolean = false,
    private val createShouldFail: Boolean = false
) : EventRepository {

    override suspend fun getAllEvents() =
        if (shouldFail) Result.failure(Exception("Network error"))
        else Result.success(events)

    override suspend fun getEvent(eventId: String) =
        if (shouldFail) Result.failure(Exception("Network error"))
        else Result.success(singleEvent)

    override suspend fun createEvent(event: Event) =
        if (createShouldFail) Result.failure(Exception("Erreur Firestore"))
        else Result.success("event-id")

    override suspend fun uploadImage(imageUri: Uri) =
        if (uploadShouldFail) Result.failure(Exception("Upload échoué"))
        else Result.success("https://example.com/image.jpg")

    override suspend fun geocodeAddress(address: String) = EventLocation(address = address)
}

class FakeUserRepository(
    private val user: User? = null,
    private val shouldFail: Boolean = false
) : UserRepository {

    override suspend fun getUser(uid: String) =
        if (shouldFail) Result.failure(Exception("Firestore error"))
        else Result.success(user)

    override suspend fun createUser(user: User) = Result.success(Unit)
    override suspend fun updateUser(uid: String, updates: Map<String, Any>) = Result.success(Unit)
    override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = Result.success(Unit)
}

class FakeAuthRepository(
    override val currentUser: FirebaseUser? = null
) : AuthRepository {
    override suspend fun signOut(context: android.content.Context) = Result.success(Unit)
}
