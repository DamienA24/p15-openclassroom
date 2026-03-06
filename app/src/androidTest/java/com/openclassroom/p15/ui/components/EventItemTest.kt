package com.openclassroom.p15.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.google.firebase.Timestamp
import com.openclassroom.p15.domain.model.Event
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class EventItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestEvent(
        title: String = "Music festival",
        date: Timestamp = Timestamp(Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 15)
        }.time)
    ): Event {
        return Event(
            id = "1",
            title = title,
            description = "A great event",
            date = date,
            creatorId = "user1",
            creatorName = "John Doe",
            imageUrl = ""
        )
    }

    @Test
    fun eventItem_displaysTitle() {
        composeTestRule.setContent {
            EventItem(
                event = createTestEvent(title = "Music festival"),
                creatorAvatarUrl = null
            )
        }

        composeTestRule
            .onNodeWithText("Music festival")
            .assertIsDisplayed()
    }

    @Test
    fun eventItem_displaysFormattedDate() {
        composeTestRule.setContent {
            EventItem(
                event = createTestEvent(),
                creatorAvatarUrl = null
            )
        }

        composeTestRule
            .onNodeWithText("June 15, 2024")
            .assertIsDisplayed()
    }
}
