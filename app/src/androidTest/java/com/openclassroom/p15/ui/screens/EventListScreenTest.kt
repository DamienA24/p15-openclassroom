package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.testutil.FakeEventRepository
import com.openclassroom.p15.testutil.FakeUserRepository
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

    private fun createViewModel(events: List<Event> = testEvents, shouldFail: Boolean = false) =
        EventViewModel(
            eventRepository = FakeEventRepository(events = events, shouldFail = shouldFail),
            userRepository = FakeUserRepository()
        )

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
            .onNodeWithText("Try again")
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
