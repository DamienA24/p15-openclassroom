package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.testutil.FakeAuthRepository
import com.openclassroom.p15.testutil.FakeUserRepository
import com.openclassroom.p15.ui.viewmodel.ProfileViewModel
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser = User(
        uid = "user1",
        email = "christopher@gmail.com",
        firstName = "Christopher",
        lastName = "Evans",
        avatarUrl = "",
        notificationsEnabled = true,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )

    private fun createViewModel(user: User? = null) = ProfileViewModel(
        authRepository = FakeAuthRepository(),
        userRepository = FakeUserRepository(user = user)
    )

    @Test
    fun profileScreen_displaysTitle() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("User profile")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysSignOutButton() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_signOutButtonShowsDialog() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {}, viewModel = createViewModel())
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
            ProfileScreen(onLogout = {}, viewModel = createViewModel())
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
            ProfileScreen(onLogout = { logoutCalled = true }, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .performClick()

        composeTestRule
            .onNodeWithText("Yes")
            .performClick()

        assert(logoutCalled)
    }

    @Test
    fun profileScreen_displaysUserName() {
        val viewModel = createViewModel(user = testUser)

        composeTestRule.setContent {
            ProfileScreen(onLogout = {}, viewModel = viewModel)
        }

        viewModel.loadUserById("user1")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Christopher Evans")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysUserEmail() {
        val viewModel = createViewModel(user = testUser)

        composeTestRule.setContent {
            ProfileScreen(onLogout = {}, viewModel = viewModel)
        }

        viewModel.loadUserById("user1")
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("christopher@gmail.com")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysNotificationsLabel() {
        composeTestRule.setContent {
            ProfileScreen(onLogout = {}, viewModel = createViewModel())
        }

        composeTestRule
            .onNodeWithText("Notifications")
            .assertIsDisplayed()
    }
}
