package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.EventLocation
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.testutil.FakeEventRepository
import com.openclassroom.p15.testutil.FakeUserRepository
import com.openclassroom.p15.ui.viewmodel.EventDetailViewModel
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class EventDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testEvent = Event(
        id = "1",
        title = "Art exhibition",
        description = "A great art show in Paris.",
        date = Timestamp(Calendar.getInstance().apply { set(2024, Calendar.JULY, 20, 10, 0, 0) }.time),
        creatorId = "user1",
        creatorName = "Emily Johnson",
        imageUrl = "",
        location = EventLocation(
            address = "123 Rue de l'Art, Paris",
            latitude = 48.8566,
            longitude = 2.3522
        )
    )

    private fun createViewModel(event: Event? = testEvent, shouldFail: Boolean = false) =
        EventDetailViewModel(
            eventRepository = FakeEventRepository(singleEvent = event, shouldFail = shouldFail),
            userRepository = FakeUserRepository(user = User(uid = "user1", avatarUrl = ""))
        )

    @Test
    fun eventDetailScreen_displaysTitle() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("Art exhibition")
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_displaysBackButton() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_backButtonCallsOnBack() {
        var backCalled = false

        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = { backCalled = true }, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backCalled)
    }

    @Test
    fun eventDetailScreen_displaysCreatorAvatar() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithContentDescription("Avatar of Emily Johnson")
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_displaysDate() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("July 20, 2024")
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_displaysDescription() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("A great art show in Paris.")
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_displaysAddress() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("123 Rue de l'Art, Paris")
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_errorState() {
        composeTestRule.setContent {
            EventDetailScreen(eventId = "1", onBack = {}, viewModel = createViewModel(shouldFail = true))
        }

        composeTestRule
            .onNodeWithText("Network error")
            .assertIsDisplayed()
    }
}
