package com.shobdodaily.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Custom Settings Theme: Cream + Yellow/Orange + Dark Navy
    val settingsColors = MaterialTheme.colorScheme.copy(
        background = Color(0xFFFFF8E1),          // Cream background
        surface = Color(0xFFFFF3E0),              // Warm cream surface
        surfaceVariant = Color(0xFFFFECB3),       // Light amber surface variant
        primary = Color(0xFF1A237E),              // Dark navy primary
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFCC80),      // Orange container
        onPrimaryContainer = Color(0xFF1A237E),    // Navy text on orange
        secondary = Color(0xFFF57C00),             // Orange accent
        onSecondary = Color.White,
        onBackground = Color(0xFF1A237E),          // Navy text on cream
        onSurface = Color(0xFF1A237E),             // Navy text on surface
        onSurfaceVariant = Color(0xFF4A4A4A),
        error = Color(0xFFD32F2F)
    )

    val context = LocalContext.current
    var clickCount by remember { mutableIntStateOf(0) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    MaterialTheme(colorScheme = settingsColors) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Notification Time
            Column {
                Text("Daily Notification Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                val amPm = if (uiState.notifyHour < 12) "AM" else "PM"
                val hour12 = if (uiState.notifyHour % 12 == 0) 12 else uiState.notifyHour % 12
                Text(String.format("%02d:00 %s", hour12, amPm), style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = uiState.notifyHour.toFloat(),
                    onValueChange = { viewModel.setNotifyHour(it.toInt()) },
                    valueRange = 0f..23f,
                    steps = 22
                )
            }

            HorizontalDivider()

            // Words Per Day
            Column {
                Text("Words Per Day", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                listOf(2, 4, 6).forEach { words ->
                    val isEnabled = words == 2 || uiState.isSubscriber
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.profile?.wordsPerDay == words,
                            onClick = { if (isEnabled) viewModel.setWordsPerDay(words) },
                            enabled = isEnabled
                        )
                        Text(
                            text = "$words words", 
                            color = if (isEnabled) Color.Unspecified else Color.Gray
                        )
                        if (!isEnabled && words > 2) {
                            Text(
                                text = " (Premium)", 
                                style = MaterialTheme.typography.labelSmall, 
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Theme
            Column {
                Text("Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("System", "Light", "Dark").forEach { themeOption ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.theme == themeOption,
                            onClick = { viewModel.setTheme(themeOption) }
                        )
                        Text(themeOption)
                    }
                }
            }

            HorizontalDivider()

            // TTS Accent
            Column {
                Text("Text-to-Speech Accent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("en-US" to "American English", "en-GB" to "British English", "en-IN" to "Indian English").forEach { (code, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.ttsAccent == code,
                            onClick = { viewModel.setTtsAccent(code) }
                        )
                        Text(label)
                    }
                }
            }

            HorizontalDivider()

            // Account Actions
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.deleteAccount()
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Account")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { throw RuntimeException("Test Crash") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Test Crash (Dev Only)")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Easter Egg Trigger
            Text(
                text = "Colonel SS Hans Lamda",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Transparent, // Very subtle, hidden unless highlighted or clicked blindly
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime > 2000) {
                            clickCount = 1
                        } else {
                            clickCount++
                        }
                        lastClickTime = currentTime

                        if (clickCount >= 7) {
                            clickCount = 0
                            viewModel.unlockEasterEgg()
                            Toast.makeText(context, "Easter Egg Unlocked!", Toast.LENGTH_LONG).show()
                        }
                    }
                    .padding(16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
    }
}
