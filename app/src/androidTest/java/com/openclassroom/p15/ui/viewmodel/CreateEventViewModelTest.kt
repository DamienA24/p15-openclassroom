package com.openclassroom.p15.ui.viewmodel

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassroom.p15.domain.model.Event
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
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
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---- Fake repositories ----

    private fun fakeEventRepo(
        uploadShouldFail: Boolean = false,
        createShouldFail: Boolean = false
    ): EventRepository = object : EventRepository {
        override suspend fun createEvent(event: Event): Result<String> =
            if (createShouldFail) Result.failure(Exception("Erreur Firestore"))
            else Result.success("event-id-123")

        override suspend fun uploadImage(imageUri: Uri): Result<String> =
            if (uploadShouldFail) Result.failure(Exception("Upload échoué"))
            else Result.success("https://example.com/image.jpg")

        override suspend fun getAllEvents() = Result.success(emptyList<Event>())
        override suspend fun getEvent(eventId: String) = Result.success(null)
    }

    private fun fakeUserRepo(): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String) = Result.success<User?>(null)
        override suspend fun createUser(user: User) = Result.success(Unit)
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = Result.success(Unit)
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = Result.success(Unit)
    }

    private fun fakeAuthRepo(): AuthRepository = object : AuthRepository {
        override val currentUser = null
        override suspend fun signOut(context: android.content.Context) = Result.success(Unit)
    }

    private fun createViewModel(
        uploadShouldFail: Boolean = false,
        createShouldFail: Boolean = false
    ) = CreateEventViewModel(
        eventRepository = fakeEventRepo(uploadShouldFail, createShouldFail),
        authRepository = fakeAuthRepo(),
        userRepository = fakeUserRepo()
    )

    // ---- Tests de validation (synchrones) ----

    @Test
    fun createEvent_withBlankTitle_setsError() {
        val viewModel = createViewModel()
        viewModel.title = ""
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = "Paris"

        viewModel.createEvent(context)

        assertEquals("Le titre est obligatoire", viewModel.error.value)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_withNoDate_setsError() {
        val viewModel = createViewModel()
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = null
        viewModel.address = "Paris"

        viewModel.createEvent(context)

        assertEquals("La date est obligatoire", viewModel.error.value)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_withBlankAddress_setsError() {
        val viewModel = createViewModel()
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = ""

        viewModel.createEvent(context)

        assertEquals("L'adresse est obligatoire", viewModel.error.value)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_withValidFields_doesNotSetValidationError() {
        val viewModel = createViewModel()
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = "Paris"

        viewModel.createEvent(context)

        assertFalse(viewModel.error.value == "Le titre est obligatoire")
        assertFalse(viewModel.error.value == "La date est obligatoire")
        assertFalse(viewModel.error.value == "L'adresse est obligatoire")
    }

    @Test
    fun createEvent_withUploadFailure_setsError() = runTest {
        val viewModel = createViewModel(uploadShouldFail = true)
        viewModel.title = "Concert"
        viewModel.selectedDateMillis = System.currentTimeMillis()
        viewModel.address = "Paris"
        viewModel.imageUri = Uri.parse("content://fake/image.jpg")

        viewModel.createEvent(context)

        assertTrue(viewModel.error.value?.contains("Échec de l'upload") == true)
        assertFalse(viewModel.eventCreated.value)
    }

    @Test
    fun createEvent_isLoadingFalseAfterValidationError() {
        val viewModel = createViewModel()
        viewModel.title = ""

        viewModel.createEvent(context)

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
