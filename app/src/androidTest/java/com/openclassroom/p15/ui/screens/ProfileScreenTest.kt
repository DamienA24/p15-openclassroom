package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_displaysTitle() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysSignOutButton() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_signOutButtonShowsDialog() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .performClick()

        composeTestRule
            .onNodeWithText("Are you sure you want to sign out?")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_signOutDialogCanBeCanceled() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .performClick()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        composeTestRule
            .onNodeWithText("Are you sure you want to sign out?")
            .assertDoesNotExist()
    }

    @Test
    fun profileScreen_signOutDialogConfirmCallsCallback() {
        var logoutCalled = false

        composeTestRule.setContent {
            ProfileScreen(onLogout = { logoutCalled = true })
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .performClick()

        composeTestRule
            .onNodeWithText("Yes")
            .performClick()

        assert(logoutCalled)
    }
}
