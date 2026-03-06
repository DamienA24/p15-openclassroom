package com.openclassroom.p15.ui.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.testutil.FakeAuthRepository
import com.openclassroom.p15.testutil.FakeUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(user: User? = User(uid = "uid1", email = "test@test.com"), shouldFail: Boolean = false) =
        ProfileViewModel(
            authRepository = FakeAuthRepository(),
            userRepository = FakeUserRepository(user = user, shouldFail = shouldFail)
        )

    @Test
    fun initialState_isClean() {
        val vm = createViewModel()
        assertNull(vm.user.value)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadUser_withNoCurrentUser_doesNothing() {
        val vm = createViewModel()
        vm.loadUser() // currentUser is null in FakeAuthRepository
        assertNull(vm.user.value)
        assertNull(vm.error.value)
    }

    @Test
    fun loadUserById_withBlankUid_doesNothing() {
        val vm = createViewModel()
        vm.loadUserById("")
        assertNull(vm.user.value)
    }

    @Test
    fun loadUserById_success_setsUser() {
        val vm = createViewModel()
        vm.loadUserById("uid1")
        assertEquals("test@test.com", vm.user.value?.email)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadUserById_failure_setsError() {
        val vm = createViewModel(shouldFail = true)
        vm.loadUserById("uid1")
        assertEquals("Firestore error", vm.error.value)
        assertNull(vm.user.value)
    }
}
