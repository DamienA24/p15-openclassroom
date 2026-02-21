package com.openclassroom.p15.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenNavigationTest {

    @Test
    fun `login screen route should be correct`() {
        assertEquals("login", Screen.Login.route)
    }

    @Test
    fun `home screen route should be correct`() {
        assertEquals("home", Screen.Home.route)
    }

    @Test
    fun `event list screen route should be correct`() {
        assertEquals("event_list", Screen.EventList.route)
    }

    @Test
    fun `profile screen route should be correct`() {
        assertEquals("profile", Screen.Profile.route)
    }

    @Test
    fun `event detail screen route should be correct`() {
        assertEquals("event_detail/{eventId}", Screen.EventDetail.route)
    }

    @Test
    fun `event detail screen createRoute should include eventId`() {
        assertEquals("event_detail/abc123", Screen.EventDetail.createRoute("abc123"))
    }
}
