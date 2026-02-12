package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
import com.openclassroom.p15.ui.viewmodel.EventViewModel
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class EventListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testEvents = listOf(
        Event(
            id = "1",
            title = "Music festival",
            description = "A great music event",
            date = Timestamp(Calendar.getInstance().apply { set(2024, Calendar.JUNE, 15) }.time),
            creatorId = "user1",
            creatorName = "Alice",
            imageUrl = ""
        ),
        Event(
            id = "2",
            title = "Art exhibition",
            description = "Modern art show",
            date = Timestamp(Calendar.getInstance().apply { set(2024, Calendar.JULY, 20) }.time),
            creatorId = "user2",
            creatorName = "Bob",
            imageUrl = ""
        ),
        Event(
            id = "3",
            title = "Tech conference",
            description = "Latest in tech",
            date = Timestamp(Calendar.getInstance().apply { set(2024, Calendar.AUGUST, 5) }.time),
            creatorId = "user1",
            creatorName = "Alice",
            imageUrl = ""
        )
    )

    private fun createFakeEventRepository(
        events: List<Event> = testEvents,
        shouldFail: Boolean = false
    ): EventRepository = object : EventRepository {
        override suspend fun getAllEvents(): Result<List<Event>> {
            return if (shouldFail) Result.failure(Exception("Network error"))
            else Result.success(events)
        }
        override suspend fun createEvent(event: Event) = TODO()
        override suspend fun getEvent(eventId: String) = TODO()
        override suspend fun getEventsByCreator(creatorId: String) = TODO()
        override suspend fun updateEvent(eventId: String, updates: Map<String, Any>) = TODO()
        override suspend fun deleteEvent(eventId: String) = TODO()
        override suspend fun getUpcomingEvents() = TODO()
        override suspend fun getPastEvents() = TODO()
    }

    private fun createFakeUserRepository(): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String): Result<User?> {
            return Result.success(User(uid = uid, avatarUrl = "https://example.com/avatar.jpg"))
        }
        override suspend fun createUser(user: User) = TODO()
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = TODO()
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = TODO()
        override suspend fun deleteUser(uid: String) = TODO()
    }

    private fun createViewModel(
        events: List<Event> = testEvents,
        shouldFail: Boolean = false
    ): EventViewModel {
        return EventViewModel(
            eventRepository = createFakeEventRepository(events, shouldFail),
            userRepository = createFakeUserRepository()
        )
    }

    @Test
    fun eventListScreen_displaysTitle() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("Event list")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_displaysSearchIcon() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Search events")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_displaysSortIcon() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Sort ascending")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_displaysFab() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Create event")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_fabCallsOnCreateEvent() {
        var createEventCalled = false

        composeTestRule.setContent {
            EventListScreen(
                onCreateEvent = { createEventCalled = true },
                eventViewModel = createViewModel()
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Create event")
            .performClick()

        assert(createEventCalled)
    }

    @Test
    fun eventListScreen_displaysEventTitles() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Music festival").assertIsDisplayed()
        composeTestRule.onNodeWithText("Art exhibition").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tech conference").assertIsDisplayed()
    }

    @Test
    fun eventListScreen_displaysEventDates() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("June 15, 2024").assertIsDisplayed()
        composeTestRule.onNodeWithText("July 20, 2024").assertIsDisplayed()
        composeTestRule.onNodeWithText("August 5, 2024").assertIsDisplayed()
    }

    @Test
    fun eventListScreen_emptyState() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel(events = emptyList()))
        }

        composeTestRule
            .onNodeWithText("No events found")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_errorState() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel(shouldFail = true))
        }

        composeTestRule
            .onNodeWithText("Network error")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_searchFiltersEvents() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Search events")
            .performClick()

        composeTestRule
            .onNodeWithText("Search events...")
            .performClick()

        composeTestRule
            .onNodeWithText("Music festival")
            .assertIsDisplayed()
    }

    @Test
    fun eventListScreen_closeSearchRestoresTitle() {
        composeTestRule.setContent {
            EventListScreen(eventViewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Search events")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Close search")
            .performClick()

        composeTestRule
            .onNodeWithText("Event list")
            .assertIsDisplayed()
    }
}
