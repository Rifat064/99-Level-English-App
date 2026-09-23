package com.shobdodaily.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.shobdodaily.core.datastore.SettingsRepository
import javax.inject.Inject
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shobdodaily.app.ui.navigation.AppNavGraph
import com.shobdodaily.core.ui.theme.ShobdoDailyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by settingsRepository.theme.collectAsState(initial = "System")
            val darkTheme = when (theme) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }
            val isEasterEggUnlocked by settingsRepository.isEasterEggUnlocked.collectAsState(initial = false)

            ShobdoDailyTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavGraph(isEasterEggUnlocked = isEasterEggUnlocked)
                }
            }
        }
    }
}
