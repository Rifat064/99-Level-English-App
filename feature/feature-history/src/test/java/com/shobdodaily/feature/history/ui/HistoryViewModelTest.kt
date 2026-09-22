package com.shobdodaily.feature.history.ui

import com.shobdodaily.core.model.HistoryCardItem
import com.shobdodaily.core.model.Profile
import com.shobdodaily.core.model.repository.CardRepository
import com.shobdodaily.core.model.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class HistoryViewModelTest {

    private lateinit var cardRepository: CardRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var viewModel: HistoryViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cardRepository = mockk()
        profileRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadHistory success groups by week and handles entitlement`() = runTest {
        val mockProfile = Profile(
            id = "user1",
            displayName = "Test",
            enrolledAt = Instant.now(),
            wordsPerDay = 2,
            timezone = "UTC",
            notifyHour = 8,
            streakCount = 0,
            longestStreak = 0,
            lastCompletedDay = 0
        )
        
        coEvery { profileRepository.getProfile() } returns Result.success(mockProfile)
        coEvery { cardRepository.syncHistory(1) } returns Result.success(Unit)
        
        val cards = listOf(
            HistoryCardItem(1, 1, 1, null, isCompleted = true, isLocked = false),
            HistoryCardItem(2, 8, 1, null, isCompleted = false, isLocked = true)
        )
        
        coEvery { cardRepository.observeHistory(1, 1, false) } returns flowOf(cards)

        viewModel = HistoryViewModel(cardRepository, profileRepository)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HistoryUiState.Success)
        
        val successState = state as HistoryUiState.Success
        assertEquals(2, successState.weeks.size)
        
        val week1 = successState.weeks[1]
        val week2 = successState.weeks[2]
        
        assertEquals(1, week1?.size)
        assertEquals(1, week1?.first()?.dayIndex)
        assertEquals(false, week1?.first()?.isLocked)

        assertEquals(1, week2?.size)
        assertEquals(8, week2?.first()?.dayIndex)
        assertEquals(true, week2?.first()?.isLocked)
    }
}
