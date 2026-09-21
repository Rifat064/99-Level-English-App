package com.shobdodaily.feature.auth.ui

import app.cash.turbine.test
import com.shobdodaily.feature.auth.domain.AuthRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val authRepository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.sessionStatus } returns MutableStateFlow<SessionStatus>(mockk<SessionStatus.NotAuthenticated>())
        every { authRepository.isUserSignedIn() } returns false
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkAuthStatus sets Success if user is signed in`() = runTest {
        val sessionStatusFlow = MutableStateFlow<SessionStatus>(mockk<SessionStatus.NotAuthenticated>())
        every { authRepository.sessionStatus } returns sessionStatusFlow
        
        val vm = AuthViewModel(authRepository)
        
        vm.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())
            vm.checkAuthStatus()
            
            sessionStatusFlow.value = mockk<SessionStatus.Authenticated>()
            assertEquals(AuthUiState.Success, awaitItem())
        }
    }

    @Test
    fun `signInAnonymously sets Success on successful auth`() = runTest {
        coEvery { authRepository.signInAnonymously() } returns Result.success(Unit)
        
        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())
            viewModel.signInAnonymously()
            
            assertEquals(AuthUiState.Loading, awaitItem())
            assertEquals(AuthUiState.Success, awaitItem())
        }
    }

    @Test
    fun `signInAnonymously sets Error on failure`() = runTest {
        coEvery { authRepository.signInAnonymously() } returns Result.failure(Exception("Network Error"))
        
        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())
            viewModel.signInAnonymously()
            
            assertEquals(AuthUiState.Loading, awaitItem())
            assertEquals(AuthUiState.Error("Network Error"), awaitItem())
        }
    }
}
