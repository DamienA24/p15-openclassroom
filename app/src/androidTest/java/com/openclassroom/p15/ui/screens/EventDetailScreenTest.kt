package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import android.net.Uri
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.EventLocation
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
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

    private fun createFakeEventRepository(
        event: Event? = testEvent,
        shouldFail: Boolean = false
    ): EventRepository = object : EventRepository {
        override suspend fun getEvent(eventId: String): Result<Event?> =
            if (shouldFail) Result.failure(Exception("Network error"))
            else Result.success(event)
        override suspend fun getAllEvents() = Result.success(emptyList<Event>())
        override suspend fun createEvent(event: Event) = TODO()
        override suspend fun uploadImage(imageUri: Uri) = TODO()
    }

    private fun createFakeUserRepository(): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String): Result<User?> =
            Result.success(User(uid = uid, avatarUrl = ""))
        override suspend fun createUser(user: User) = TODO()
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = TODO()
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = TODO()
    }

    private fun createViewModel(
        event: Event? = testEvent,
        shouldFail: Boolean = false
    ): EventDetailViewModel = EventDetailViewModel(
        eventRepository = createFakeEventRepository(event, shouldFail),
        userRepository = createFakeUserRepository()
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
