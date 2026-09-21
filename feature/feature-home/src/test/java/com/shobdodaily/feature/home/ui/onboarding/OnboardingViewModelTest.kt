package com.shobdodaily.feature.home.ui.onboarding

import com.shobdodaily.core.datastore.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val settingsRepository = mockk<SettingsRepository>()
    private lateinit var viewModel: OnboardingViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { settingsRepository.notificationHour } returns MutableStateFlow(8)
        viewModel = OnboardingViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `completeOnboarding calls repository`() = runTest {
        coEvery { settingsRepository.completeOnboarding() } returns Unit

        viewModel.completeOnboarding()
        
        // Let coroutines finish
        testScheduler.advanceUntilIdle()

        coVerify { settingsRepository.completeOnboarding() }
    }

    @Test
    fun `setNotificationHour calls repository`() = runTest {
        coEvery { settingsRepository.setNotificationHour(10) } returns Unit

        viewModel.setNotificationHour(10)
        
        // Let coroutines finish
        testScheduler.advanceUntilIdle()

        coVerify { settingsRepository.setNotificationHour(10) }
    }
}
