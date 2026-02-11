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
}
