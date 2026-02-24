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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class EventViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val events = listOf(
        Event(id = "1", title = "Concert", date = Timestamp(Date(2000L))),
        Event(id = "2", title = "Festival", date = Timestamp(Date(3000L))),
        Event(id = "3", title = "Expo", date = Timestamp(Date(1000L)))
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
        result: List<Event> = events,
        shouldFail: Boolean = false
    ): EventRepository = object : EventRepository {
        override suspend fun getAllEvents() =
            if (shouldFail) Result.failure(Exception("Network error"))
            else Result.success(result)
        override suspend fun createEvent(event: Event) = Result.success("")
        override suspend fun uploadImage(imageUri: Uri) = Result.success("")
        override suspend fun getEvent(eventId: String) = Result.success(null)
    }

    private fun fakeUserRepo(): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String) = Result.success<User?>(null)
        override suspend fun createUser(user: User) = Result.success(Unit)
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = Result.success(Unit)
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = Result.success(Unit)
    }

    private fun createViewModel(shouldFail: Boolean = false) =
        EventViewModel(fakeEventRepo(shouldFail = shouldFail), fakeUserRepo())

    @Test
    fun loadEvents_success_setsFilteredEvents() {
        val vm = createViewModel()
        assertEquals(3, vm.filteredEvents.value.size)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadEvents_failure_setsError() {
        val vm = createViewModel(shouldFail = true)
        assertEquals("Network error", vm.error.value)
        assertTrue(vm.filteredEvents.value.isEmpty())
    }

    @Test
    fun onSearchQueryChanged_filtersEventsByTitle() {
        val vm = createViewModel()
        vm.onSearchQueryChanged("Conc")
        assertEquals(1, vm.filteredEvents.value.size)
        assertEquals("Concert", vm.filteredEvents.value.first().title)
    }

    @Test
    fun onSearchQueryChanged_emptyQuery_returnsAll() {
        val vm = createViewModel()
        vm.onSearchQueryChanged("Conc")
        vm.onSearchQueryChanged("")
        assertEquals(3, vm.filteredEvents.value.size)
    }

    @Test
    fun toggleSortOrder_sortsByDateDescending() {
        val vm = createViewModel()
        assertTrue(vm.sortAscending.value)
        vm.toggleSortOrder()
        assertFalse(vm.sortAscending.value)
        assertEquals("Festival", vm.filteredEvents.value.first().title)
    }

    @Test
    fun toggleSortOrder_twice_restoresAscending() {
        val vm = createViewModel()
        vm.toggleSortOrder()
        vm.toggleSortOrder()
        assertTrue(vm.sortAscending.value)
        assertEquals("Expo", vm.filteredEvents.value.first().title)
    }
}
