package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.openclassroom.p15.testutil.FakeAuthRepository
import com.openclassroom.p15.testutil.FakeEventRepository
import com.openclassroom.p15.testutil.FakeUserRepository
import com.openclassroom.p15.ui.viewmodel.CreateEventViewModel
import org.junit.Rule
import org.junit.Test

class CreateEventScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel() = CreateEventViewModel(
        eventRepository = FakeEventRepository(),
        authRepository = FakeAuthRepository(),
        userRepository = FakeUserRepository()
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
