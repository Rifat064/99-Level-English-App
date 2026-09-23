package com.shobdodaily.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand palette - educational, focused, premium
val ClinicalYellowGreenLight = Color(0xFFA5D6A7) // Clinical yellow‑green (soft pastel)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE2E8F0)
val OnPrimaryContainerLight = Color(0xFF0F172A)

val SecondaryLight = Color(0xFFEA580C) // Soft Orange / Gold
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFEDD5)
val OnSecondaryContainerLight = Color(0xFF9A3412)

val TertiaryLight = Color(0xFF059669) // Emerald Green for learned / success
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFD1FAE5)
val OnTertiaryContainerLight = Color(0xFF064E3B)

val BackgroundLight = Color(0xFFF8FAFC) // Soft off-white
val OnBackgroundLight = Color(0xFF0F172A) // Navy text
val SurfaceLight = Color(0xFFFFFFFF) // White cards
val OnSurfaceLight = Color(0xFF0F172A)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnSurfaceVariantLight = Color(0xFF475569) // Gray paragraph text
val CardNeutralSurfaceLight = Color(0xFFFFFFFF)

// Dark theme (Keeping mostly the same, but slightly tweaking primary for consistency if user ever switches)
val ClinicalYellowGreenDark = Color(0xFF6D9C5F) // Darker shade of clinical yellow‑green
val OnPrimaryDark = Color(0xFF0F172A)
val PrimaryContainerDark = Color(0xFF1E293B)
val OnPrimaryContainerDark = Color(0xFFF8FAFC)

val SecondaryDark = Color(0xFFF97316)
val OnSecondaryDark = Color(0xFFFFFFFF)
val SecondaryContainerDark = Color(0xFF7C2D12)
val OnSecondaryContainerDark = Color(0xFFFFEDD5)

val TertiaryDark = Color(0xFF34D399)
val OnTertiaryDark = Color(0xFF064E3B)
val TertiaryContainerDark = Color(0xFF065F46)
val OnTertiaryContainerDark = Color(0xFFD1FAE5)

val BackgroundDark = Color(0xFF0F172A)
val OnBackgroundDark = Color(0xFFF8FAFC)
val SurfaceDark = Color(0xFF1E293B)
val OnSurfaceDark = Color(0xFFF8FAFC)
val SurfaceVariantDark = Color(0xFF334155)
val OnSurfaceVariantDark = Color(0xFF94A3B8)
val CardNeutralSurfaceDark = Color(0xFF1E293B)

val LightColorScheme =
    lightColorScheme(
        primary = ClinicalYellowGreenLight,
        onPrimary = OnPrimaryLight,
        primaryContainer = PrimaryContainerLight,
        onPrimaryContainer = OnPrimaryContainerLight,
        secondary = SecondaryLight,
        onSecondary = OnSecondaryLight,
        secondaryContainer = SecondaryContainerLight,
        onSecondaryContainer = OnSecondaryContainerLight,
        tertiary = TertiaryLight,
        onTertiary = OnTertiaryLight,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerLight,
        background = BackgroundLight,
        onBackground = OnBackgroundLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = OnSurfaceVariantLight,
    )

val DarkColorScheme =
    darkColorScheme(
        primary = ClinicalYellowGreenDark,
        onPrimary = OnPrimaryDark,
        primaryContainer = PrimaryContainerDark,
        onPrimaryContainer = OnPrimaryContainerDark,
        secondary = SecondaryDark,
        onSecondary = OnSecondaryDark,
        secondaryContainer = SecondaryContainerDark,
        onSecondaryContainer = OnSecondaryContainerDark,
        tertiary = TertiaryDark,
        onTertiary = OnTertiaryDark,
        tertiaryContainer = TertiaryContainerDark,
        onTertiaryContainer = OnTertiaryContainerDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,
    )
