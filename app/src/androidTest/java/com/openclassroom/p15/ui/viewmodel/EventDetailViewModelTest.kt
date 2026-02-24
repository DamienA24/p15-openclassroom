package com.openclassroom.p15.ui.viewmodel

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class EventDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testEvent = Event(
        id = "1",
        title = "Art exhibition",
        creatorId = "user1",
        date = Timestamp.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeEventRepo(
        event: Event? = testEvent,
        shouldFail: Boolean = false
    ): EventRepository = object : EventRepository {
        override suspend fun getEvent(eventId: String) =
            if (shouldFail) Result.failure(Exception("Network error"))
            else Result.success(event)
        override suspend fun getAllEvents() = Result.success(emptyList<Event>())
        override suspend fun createEvent(event: Event) = Result.success("")
        override suspend fun uploadImage(imageUri: Uri) = Result.success("")
    }

    private fun fakeUserRepo(avatarUrl: String = ""): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String) =
            Result.success<User?>(User(uid = uid, avatarUrl = avatarUrl))
        override suspend fun createUser(user: User) = Result.success(Unit)
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = Result.success(Unit)
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = Result.success(Unit)
    }

    private fun createViewModel(event: Event? = testEvent, shouldFail: Boolean = false) =
        EventDetailViewModel(fakeEventRepo(event, shouldFail), fakeUserRepo())

    @Test
    fun initialState_isClean() {
        val vm = createViewModel()
        assertNull(vm.event.value)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadEvent_success_setsEvent() {
        val vm = createViewModel()
        vm.loadEvent("1")
        assertNotNull(vm.event.value)
        assertEquals("Art exhibition", vm.event.value?.title)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadEvent_failure_setsError() {
        val vm = createViewModel(shouldFail = true)
        vm.loadEvent("1")
        assertEquals("Network error", vm.error.value)
        assertNull(vm.event.value)
    }

    @Test
    fun loadEvent_notFound_setsError() {
        val vm = createViewModel(event = null)
        vm.loadEvent("unknown")
        assertEquals("Event not found", vm.error.value)
        assertNull(vm.event.value)
    }

    @Test
    fun loadEvent_withCreatorAvatar_setsAvatarUrl() {
        val vm = EventDetailViewModel(
            fakeEventRepo(),
            fakeUserRepo(avatarUrl = "https://example.com/avatar.jpg")
        )
        vm.loadEvent("1")
        assertEquals("https://example.com/avatar.jpg", vm.creatorAvatarUrl.value)
    }
}
