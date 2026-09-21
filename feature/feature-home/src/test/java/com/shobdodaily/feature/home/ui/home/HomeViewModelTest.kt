package com.shobdodaily.feature.home.ui.home

import com.shobdodaily.core.model.Profile
import com.shobdodaily.core.model.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val profileRepository = mockk<ProfileRepository>()
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile transitions to Success when logged in`() = runTest {
        val testProfile = Profile(
            id = "test-id",
            displayName = "Test User",
            enrolledAt = Instant.now(),
            wordsPerDay = 2,
            timezone = "UTC",
            notifyHour = 8,
            streakCount = 0,
            longestStreak = 0,
            lastCompletedDay = 0
        )
        
        coEvery { profileRepository.getProfile() } returns Result.success(testProfile)

        viewModel = HomeViewModel(profileRepository)
        
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals("Test User", (state as HomeUiState.Success).profile.displayName)
        assertEquals(1, state.currentDayIndex)
    }

    @Test
    fun `loadProfile transitions to Error when profile fetching fails`() = runTest {
        coEvery { profileRepository.getProfile() } returns Result.failure(Exception("Failed"))

        viewModel = HomeViewModel(profileRepository)
        
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals("Failed", (state as HomeUiState.Error).message)
    }
}
