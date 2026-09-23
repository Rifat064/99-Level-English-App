package com.shobdodaily.feature.home.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun OmnitrixHomeScreen(
    onNavigateToCard: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var splashTarget by remember { mutableStateOf<(() -> Unit)?>(null) }
    val splashRadius = remember { Animatable(0f) }

    LaunchedEffect(splashTarget) {
        if (splashTarget != null) {
            splashRadius.animateTo(
                targetValue = 2000f,
                animationSpec = tween(durationMillis = 600)
            )
            splashTarget?.invoke()
            splashRadius.snapTo(0f)
            splashTarget = null
        }
    }

    Scaffold(
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "OMNITRIX MODE",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF00FF00),
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(48.dp))

                // Circular Dial Layout (Simplified)
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .clickable {
                            if (splashTarget == null) {
                                splashTarget = onNavigateToCard
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF00)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "START",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Simple indicators
                    Text("QUIZ", color = Color.White, modifier = Modifier.align(Alignment.TopCenter).padding(16.dp).clickable { if (splashTarget == null) splashTarget = onNavigateToQuiz })
                    Text("SET", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).clickable { if (splashTarget == null) splashTarget = onNavigateToSettings })
                }
            }

            if (splashTarget != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFF00FF00),
                        radius = splashRadius.value,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }
            }
        }
    }
}
