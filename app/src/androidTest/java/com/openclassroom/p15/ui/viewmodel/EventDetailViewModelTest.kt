package com.openclassroom.p15.ui.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.EventLocation
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.testutil.FakeEventRepository
import com.openclassroom.p15.testutil.FakeUserRepository
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
        date = Timestamp.now(),
        location = EventLocation(address = "Paris", latitude = 48.8566, longitude = 2.3522)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(event: Event? = testEvent, shouldFail: Boolean = false, avatarUrl: String = "") =
        EventDetailViewModel(
            eventRepository = FakeEventRepository(singleEvent = event, shouldFail = shouldFail),
            userRepository = FakeUserRepository(user = if (avatarUrl.isNotBlank()) User(uid = "user1", avatarUrl = avatarUrl) else null)
        )

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
        val vm = createViewModel(avatarUrl = "https://example.com/avatar.jpg")
        vm.loadEvent("1")
        assertEquals("https://example.com/avatar.jpg", vm.creatorAvatarUrl.value)
    }
}
