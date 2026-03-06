package com.openclassroom.p15.ui.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
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

    private fun createViewModel(shouldFail: Boolean = false) =
        EventViewModel(
            eventRepository = FakeEventRepository(events = events, shouldFail = shouldFail),
            userRepository = FakeUserRepository()
        )

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
