package com.shobdodaily.app.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.shobdodaily.core.ui.navigation.DailyCard
import com.shobdodaily.core.ui.navigation.History
import com.shobdodaily.core.ui.navigation.Home
import com.shobdodaily.core.ui.navigation.Login
import com.shobdodaily.core.ui.navigation.Paywall
import com.shobdodaily.core.ui.navigation.Quiz
import com.shobdodaily.core.ui.navigation.Settings
import com.shobdodaily.core.ui.navigation.Splash
import com.shobdodaily.core.ui.navigation.Dictionary
import com.shobdodaily.feature.auth.ui.LoginScreen
import com.shobdodaily.feature.history.dictionary.DictionaryScreen
import com.shobdodaily.feature.home.ui.home.HomeScreen
import com.shobdodaily.feature.home.ui.onboarding.OnboardingScreen
import com.shobdodaily.app.ui.settings.SettingsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Splash,
        modifier = modifier
    ) {
        composable<Splash> {
            val splashViewModel = hiltViewModel<SplashViewModel>()
            val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                when (uiState) {
                    is SplashUiState.GoToLogin -> {
                        navController.navigate(Login) {
                            popUpTo(Splash) { inclusive = true }
                        }
                    }
                    is SplashUiState.GoToOnboarding -> {
                        navController.navigate(com.shobdodaily.core.ui.navigation.Onboarding) {
                            popUpTo(Splash) { inclusive = true }
                        }
                    }
                    is SplashUiState.GoToHome -> {
                        navController.navigate(Home) {
                            popUpTo(Splash) { inclusive = true }
                        }
                    }
                    else -> {}
                }
            }

            PlaceholderScreen("Splash")
        }

        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Splash) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<com.shobdodaily.core.ui.navigation.Onboarding> {
            OnboardingScreen(
                onOnboardingCompleted = {
                    navController.navigate(Home) {
                        popUpTo(com.shobdodaily.core.ui.navigation.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen(
                onNavigateToCard = { dayIndex ->
                    navController.navigate(DailyCard(dayIndex))
                }
            )
        }

        composable<DailyCard>(
            deepLinks = listOf(
                navDeepLink<DailyCard>(basePath = "shobdodaily://card")
            )
        ) { backStackEntry ->
            // In navigation compose 2.8 with type-safe nav, args are extracted natively
            // Unfortunately, without the type-safe feature active right here via reified types in a separate way, we just know it routes here.
            PlaceholderScreen("DailyCard")
        }

        composable<Quiz> {
            com.shobdodaily.feature.quiz.presentation.QuizRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<History> {
            com.shobdodaily.feature.history.ui.HistoryScreen(
                onCardClick = { dayIndex ->
                    navController.navigate(DailyCard(dayIndex))
                },
                onNavigateToReview = {
                    navController.navigate(com.shobdodaily.core.ui.navigation.ReviewDeck)
                },
                onNavigateToDictionary = {
                    navController.navigate(Dictionary)
                }
            )
        }

        composable<Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Splash) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Paywall> {
            PlaceholderScreen("Paywall")
        }

        composable<com.shobdodaily.core.ui.navigation.ReviewDeck> {
            com.shobdodaily.feature.history.review.ReviewDeckRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Dictionary> {
            DictionaryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (onClick != null) {
            androidx.compose.material3.Button(onClick = onClick) {
                Text(text = "Navigate from $title")
            }
        } else {
            Text(text = title)
        }
    }
}
