package com.openclassroom.p15.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.openclassroom.p15.R
import org.junit.Rule
import org.junit.Test

class AuthButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun authButton_displaysTextCorrectly() {
        val buttonText = "Sign in with Google"

        composeTestRule.setContent {
            AuthButton(
                text = buttonText,
                logoResId = R.drawable.googlelogo,
                containerColor = Color.Blue,
                contentColor = Color.White,
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
    }

    @Test
    fun authButton_clickTriggersCallback() {
        var clicked = false
        val buttonText = "Sign in with Email"

        composeTestRule.setContent {
            MaterialTheme {
                AuthButton(
                    text = buttonText,
                    logoResId = R.drawable.emaillogo,
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText(buttonText)
            .performClick()

        assert(clicked)
    }

    @Test
    fun authButton_hasCorrectContentDescription() {
        val buttonText = "Sign in with Google"

        composeTestRule.setContent {
            AuthButton(
                text = buttonText,
                logoResId = R.drawable.googlelogo,
                containerColor = Color.Blue,
                contentColor = Color.White,
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription(buttonText)
            .assertIsDisplayed()
    }
}
