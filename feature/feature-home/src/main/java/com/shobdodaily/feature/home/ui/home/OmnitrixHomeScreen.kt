package com.shobdodaily.feature.home.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OmnitrixHomeScreen(
    onNavigateToCard: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showSplash by remember { mutableStateOf(false) }
    var targetNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val splashRadius = remember { Animatable(0f) }

    fun triggerSplash(navigate: () -> Unit) {
        targetNavigation = navigate
        showSplash = true
        coroutineScope.launch {
            splashRadius.animateTo(
                targetValue = 1000f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
            targetNavigation?.invoke()
            showSplash = false
            splashRadius.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Hologram / Omnitrix Layout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ALIEN DNA READY",
                color = Color(0xFF00FF00), // Neon Green
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(48.dp))

            // The Omnitrix Dial
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .background(Color(0xFF111111), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Inner green core
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFF003300), CircleShape)
                        .clickable { triggerSplash(onNavigateToCard) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "START",
                        color = Color(0xFF00FF00),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(
                    onClick = { triggerSplash(onNavigateToProgress) },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF003300), CircleShape)
                ) {
                    Icon(Icons.Default.List, contentDescription = "Progress", tint = Color(0xFF00FF00))
                }

                IconButton(
                    onClick = { triggerSplash(onNavigateToSettings) },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF003300), CircleShape)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF00FF00))
                }
            }
        }

        // Alien DNA Splash Overlay
        if (showSplash) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF00FF00).copy(alpha = 0.8f),
                    radius = splashRadius.value,
                    center = center
                )
            }
        }
    }
}
