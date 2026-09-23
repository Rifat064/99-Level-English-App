package com.shobdodaily.app.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.padding
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
import com.shobdodaily.app.ui.splash.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.List
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isEasterEggUnlocked: Boolean = false
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute(Home::class) == true ||
            currentDestination?.hasRoute(History::class) == true ||
            currentDestination?.hasRoute(Settings::class) == true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Home::class) == true,
                        onClick = {
                            navController.navigate(Home) {
                                popUpTo(Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(History::class) == true,
                        onClick = {
                            navController.navigate(History) {
                                popUpTo(Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Progress") },
                        label = { Text("Progress") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Settings::class) == true,
                        onClick = {
                            navController.navigate(Settings) {
                                popUpTo(Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Splash,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
        composable<Splash> {
            val splashViewModel = hiltViewModel<SplashViewModel>()
            val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()
            var isSplashAnimationFinished by remember { mutableStateOf(false) }

            LaunchedEffect(uiState, isSplashAnimationFinished) {
                if (isSplashAnimationFinished) {
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
            }

            SplashScreen(
                onSplashFinished = {
                    isSplashAnimationFinished = true
                }
            )
        }

        composable<Login> {
            if (isEasterEggUnlocked) {
                com.shobdodaily.feature.auth.ui.EasterEggLoginScreen(
                    onSignInClick = { 
                        navController.navigate(Splash) {
                            popUpTo(Login) { inclusive = true }
                        }
                    },
                    onGuestClick = {
                        navController.navigate(com.shobdodaily.core.ui.navigation.Onboarding) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                )
            } else {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Splash) {
                            popUpTo(Login) { inclusive = true }
                        }
                    },
                    onGuestContinue = {
                        navController.navigate(com.shobdodaily.core.ui.navigation.Onboarding) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<com.shobdodaily.core.ui.navigation.Onboarding> {
            com.shobdodaily.feature.home.ui.onboarding.WalkthroughScreen(
                onWalkthroughCompleted = {
                    navController.navigate(Home) {
                        popUpTo(com.shobdodaily.core.ui.navigation.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            if (isEasterEggUnlocked) {
                var showIntro by remember { mutableStateOf(true) }
                if (showIntro) {
                    com.shobdodaily.feature.home.ui.home.WhoAreYouScreen(
                        onContinue = { showIntro = false }
                    )
                } else {
                    com.shobdodaily.feature.home.ui.home.OmnitrixHomeScreen(
                        onNavigateToCard = {
                            navController.navigate(DailyCard(1))
                        },
                        onNavigateToProgress = {
                            navController.navigate(History)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Settings)
                        }
                    )
                }
            } else {
                HomeScreen(
                    onNavigateToCard = { dayIndex ->
                        navController.navigate(DailyCard(dayIndex))
                    }
                )
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
            if (isEasterEggUnlocked) {
                com.shobdodaily.feature.quiz.ui.Ben10QuizScreen(
                    word = "Ostentatious",
                    options = listOf("Showy", "Humble", "Quiet", "Simple"),
                    correctOption = "Showy",
                    onCorrectAnswer = {},
                    onWrongAnswer = {},
                    onNext = { navController.popBackStack() }
                )
            } else {
                com.shobdodaily.feature.quiz.presentation.QuizRoute(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
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
