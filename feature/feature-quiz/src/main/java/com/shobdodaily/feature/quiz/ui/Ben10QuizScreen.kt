package com.shobdodaily.feature.quiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Ben10QuizScreen(
    word: String,
    options: List<String>,
    correctOption: String,
    onCorrectAnswer: () -> Unit,
    onWrongAnswer: () -> Unit,
    onNext: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedOption != null && isCorrect) {
            // Clash of Clans Success State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.DarkGray, shape = MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[Insert CoC Chief Girl Image Here]\nR.drawable.img_coc_chief",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Excellent Work, Chief!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFFC107), // Gold
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Next Word")
            }
        } else {
            // Ben 10 Question State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.DarkGray, shape = MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[Insert Ben 10 Image Here]\nR.drawable.img_ben10",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "What is the meaning of:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray
            )
            Text(
                text = word,
                style = MaterialTheme.typography.displayMedium,
                color = Color(0xFF00FF00), // Neon green
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { option ->
                Button(
                    onClick = {
                        selectedOption = option
                        if (option == correctOption) {
                            isCorrect = true
                            onCorrectAnswer()
                        } else {
                            onWrongAnswer()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == option && option != correctOption) Color.Red else Color(0xFF111111)
                    )
                ) {
                    Text(option, color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
