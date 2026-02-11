package com.openclassroom.p15.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysWelcomeText() {
        composeTestRule.setContent {
            HomeScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithText("Bienvenue !")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysTopBarTitle() {
        composeTestRule.setContent {
            HomeScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithText("Eventorias")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_hasLogoutButton() {
        composeTestRule.setContent {
            HomeScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithContentDescription("Déconnexion")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_logoutButtonShowsDialog() {
        composeTestRule.setContent {
            HomeScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithContentDescription("Déconnexion")
            .performClick()

        composeTestRule
            .onNodeWithText("Voulez-vous vraiment vous déconnecter ?")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_logoutDialogHasCancelButton() {
        composeTestRule.setContent {
            HomeScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithContentDescription("Déconnexion")
            .performClick()

        composeTestRule
            .onNodeWithText("Annuler")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_logoutDialogCanBeCanceled() {
        composeTestRule.setContent {
            HomeScreen(onLogout = {})
        }

        composeTestRule
            .onNodeWithContentDescription("Déconnexion")
            .performClick()

        composeTestRule
            .onNodeWithText("Annuler")
            .performClick()

        composeTestRule
            .onNodeWithText("Voulez-vous vraiment vous déconnecter ?")
            .assertDoesNotExist()
    }
}
