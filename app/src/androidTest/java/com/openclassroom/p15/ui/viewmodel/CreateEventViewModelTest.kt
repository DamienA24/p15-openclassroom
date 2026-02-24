package com.openclassroom.p15.ui.viewmodel

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.p15.testutil.FakeAuthRepository
import com.openclassroom.p15.testutil.FakeEventRepository
import com.openclassroom.p15.testutil.FakeUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CreateEventViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        uploadShouldFail: Boolean = false,
        createShouldFail: Boolean = false
    ) = CreateEventViewModel(
        eventRepository = FakeEventRepository(uploadShouldFail = uploadShouldFail, createShouldFail = createShouldFail),
        authRepository = FakeAuthRepository(),
        userRepository = FakeUserRepository()
    )

    // ---- Tests de validation (synchrones) ----

    @Test
    fun createEvent_withBlankTitle_setsError() {
        val viewModel = createViewModel()
        viewModel.title = ""
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = "Paris"

        viewModel.createEvent()

        assertEquals("Le titre est obligatoire", viewModel.error.value)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_withNoDate_setsError() {
        val viewModel = createViewModel()
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = null
        viewModel.address = "Paris"

        viewModel.createEvent()

        assertEquals("La date est obligatoire", viewModel.error.value)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_withBlankAddress_setsError() {
        val viewModel = createViewModel()
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = ""

        viewModel.createEvent()

        assertEquals("L'adresse est obligatoire", viewModel.error.value)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_withValidFields_doesNotSetValidationError() {
        val viewModel = createViewModel()
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = "Paris"

        viewModel.createEvent()

        assertFalse(viewModel.error.value == "Le titre est obligatoire")
        assertFalse(viewModel.error.value == "La date est obligatoire")
        assertFalse(viewModel.error.value == "L'adresse est obligatoire")
    }

    // ---- Tests asynchrones ----

    @Test
    fun createEvent_withUploadFailure_setsError() = runTest {
        val viewModel = createViewModel(uploadShouldFail = true)
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = "Paris"
        viewModel.imageUri = Uri.parse("content://fake/image.jpg")

        viewModel.createEvent()

        assertTrue(viewModel.error.value?.contains("Échec de l'upload") == true)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_isLoadingFalseAfterValidationError() {
        val viewModel = createViewModel()
        viewModel.title = ""

        viewModel.createEvent()

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun createEvent_initialState_isClean() {
        val viewModel = createViewModel()

        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.eventCreated.value)
        assertEquals("", viewModel.title)
        assertEquals("", viewModel.description)
        assertEquals("", viewModel.address)
        assertNull(viewModel.selectedDateMillis)
        assertNull(viewModel.imageUri)
    }
}
