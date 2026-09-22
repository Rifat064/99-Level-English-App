package com.shobdodaily.core.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination

@Serializable
object Splash : Destination

@Serializable
object Login : Destination

@Serializable
object Onboarding : Destination

@Serializable
object Home : Destination

@Serializable
data class DailyCard(val dayIndex: Int) : Destination

@Serializable
data class Quiz(val weekIndex: Int) : Destination

@Serializable
object History : Destination

@Serializable
object Settings : Destination

@Serializable
object Paywall : Destination

@Serializable
object ReviewDeck : Destination
