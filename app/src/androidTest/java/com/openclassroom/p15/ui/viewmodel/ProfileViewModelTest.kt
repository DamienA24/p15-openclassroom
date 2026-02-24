package com.openclassroom.p15.ui.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.p15.domain.model.User
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
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

    private fun fakeUserRepo(
        user: User? = User(uid = "uid1", email = "test@test.com"),
        shouldFail: Boolean = false
    ): UserRepository = object : UserRepository {
        override suspend fun getUser(uid: String) =
            if (shouldFail) Result.failure(Exception("Firestore error"))
            else Result.success(user)
        override suspend fun createUser(user: User) = Result.success(Unit)
        override suspend fun updateUser(uid: String, updates: Map<String, Any>) = Result.success(Unit)
        override suspend fun updateNotificationPreference(uid: String, enabled: Boolean) = Result.success(Unit)
    }

    private fun fakeAuthRepo(firebaseUser: FirebaseUser? = null): AuthRepository =
        object : AuthRepository {
            override val currentUser: FirebaseUser? = firebaseUser
            override suspend fun signOut(context: android.content.Context) = Result.success(Unit)
        }

    @Test
    fun initialState_isClean() {
        val vm = ProfileViewModel(fakeAuthRepo(), fakeUserRepo())
        assertNull(vm.user.value)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadUser_withNoCurrentUser_doesNothing() {
        val vm = ProfileViewModel(fakeAuthRepo(firebaseUser = null), fakeUserRepo())
        vm.loadUser()
        assertNull(vm.user.value)
        assertNull(vm.error.value)
    }

    @Test
    fun loadUserById_withBlankUid_doesNothing() {
        val vm = ProfileViewModel(fakeAuthRepo(), fakeUserRepo())
        vm.loadUserById("")
        assertNull(vm.user.value)
    }

    @Test
    fun loadUserById_success_setsUser() {
        val vm = ProfileViewModel(fakeAuthRepo(), fakeUserRepo())
        vm.loadUserById("uid1")
        assertEquals("test@test.com", vm.user.value?.email)
        assertNull(vm.error.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun loadUserById_failure_setsError() {
        val vm = ProfileViewModel(fakeAuthRepo(), fakeUserRepo(shouldFail = true))
        vm.loadUserById("uid1")
        assertEquals("Firestore error", vm.error.value)
        assertNull(vm.user.value)
    }
}
