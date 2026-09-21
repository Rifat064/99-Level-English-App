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
import com.shobdodaily.feature.auth.ui.LoginScreen
import com.shobdodaily.feature.home.ui.onboarding.OnboardingScreen
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
            PlaceholderScreen("Home") {
                // Navigate to DailyCard for dayIndex = 1 as a placeholder action
                navController.navigate(DailyCard(1))
            }
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
            PlaceholderScreen("Quiz")
        }

        composable<History> {
            PlaceholderScreen("History")
        }

        composable<Settings> {
            PlaceholderScreen("Settings")
        }

        composable<Paywall> {
            PlaceholderScreen("Paywall")
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
