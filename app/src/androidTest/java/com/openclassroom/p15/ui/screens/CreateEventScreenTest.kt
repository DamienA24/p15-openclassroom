package com.openclassroom.p15.ui.screens

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
import com.openclassroom.p15.ui.viewmodel.CreateEventViewModel
import org.junit.Rule
import org.junit.Test

class CreateEventScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---- Fake repositories ----

    private fun createFakeEventRepository(): EventRepository = object : EventRepository {
        override suspend fun createEvent(event: Event) = Result.success("event-id")
        override suspend fun uploadImage(imageUri: Uri) = Result.success("https://example.com/img.jpg")
        override suspend fun getAllEvents() = Result.success(emptyList<Event>())
        override suspend fun getEvent(eventId: String) = Result.success(null)
    }

    private fun createFakeUserRepository(): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String) = Result.success<User?>(null)
        override suspend fun createUser(user: User) = Result.success(Unit)
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = Result.success(Unit)
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = Result.success(Unit)
    }

    private fun createFakeAuthRepository(): AuthRepository = object : AuthRepository {
        override val currentUser = null
        override suspend fun signOut(context: android.content.Context) = Result.success(Unit)
    }

    private fun createViewModel() = CreateEventViewModel(
        eventRepository = createFakeEventRepository(),
        authRepository = createFakeAuthRepository(),
        userRepository = createFakeUserRepository()
    )

    // ---- Tests d'affichage ----

    @Test
    fun createEventScreen_displaysTitle() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Creation of an event").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysBackButton() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysTitleField() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysDescriptionField() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysDateField() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Date").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysTimeField() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Time").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysAddressField() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Address").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysValidateButton() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Validate").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysCameraButton() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithContentDescription("Prendre une photo").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_displaysGalleryButton() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithContentDescription("Choisir depuis la galerie").assertIsDisplayed()
    }

    // ---- Tests d'interaction ----

    @Test
    fun createEventScreen_backButtonCallsOnBack() {
        var backCalled = false

        composeTestRule.setContent {
            CreateEventScreen(onBack = { backCalled = true }, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assert(backCalled)
    }

    @Test
    fun createEventScreen_validateButtonIsEnabled() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Validate").assertIsEnabled()
    }

    @Test
    fun createEventScreen_validateWithEmptyForm_showsTitleError() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Validate").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Le titre est obligatoire").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_titleInputIsReflectedInField() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Title").performTextInput("Festival de jazz")
        composeTestRule.onNodeWithText("Festival de jazz").assertIsDisplayed()
    }

    @Test
    fun createEventScreen_validateAfterFillingTitle_showsDateError() {
        composeTestRule.setContent {
            CreateEventScreen(onBack = {}, onEventCreated = {}, viewModel = createViewModel())
        }

        composeTestRule.onNodeWithText("Title").performTextInput("Mon événement")
        composeTestRule.onNodeWithText("Validate").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("La date est obligatoire").assertIsDisplayed()
    }
}
