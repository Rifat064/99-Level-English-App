package com.shobdodaily.feature.quiz.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ben10QuizScreen(
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF1E1E1E), // Dark theme
        topBar = {
            TopAppBar(
                title = { 
                    Text("HERO TIME (Ben 10 Quiz)", color = Color(0xFF00FF00), fontWeight = FontWeight.Bold) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            if (uiState is QuizUiState.ActiveQuestion) {
                Button(
                    onClick = onSubmitAnswer,
                    enabled = uiState.selectedOptionIndex != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) "FINISH" else "NEXT ALIEN",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (uiState is QuizUiState.Finished) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    )
                ) {
                    Text("RETURN TO OMNITRIX", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is QuizUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF00FF00)
                    )
                }
                is QuizUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is QuizUiState.Finished -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // CoC Girl Placeholder on completion
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.DarkGray, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/ShobdoDaily/assets/main/coc_girl_placeholder.png",
                                contentDescription = "Clash of Clans Girl",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "Placeholder:\nCoC Girl congratulating\n(Replace with real image)",
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        QuizResultsScreen(
                            score = uiState.score,
                            totalQuestions = uiState.totalQuestions,
                            missedWords = uiState.missedWords
                        )
                    }
                }
                is QuizUiState.ActiveQuestion -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LinearProgressIndicator(
                            progress = { uiState.currentQuestionIndex / uiState.totalQuestions.toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF00FF00),
                            trackColor = Color.DarkGray
                        )
                        
                        // Ben 10 Placeholder asking
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(16.dp)
                                .background(Color.DarkGray, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/ShobdoDaily/assets/main/ben10_placeholder.png",
                                contentDescription = "Ben 10",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "Placeholder:\nBen 10 asking the meaning\n(Replace with real image)",
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                        
                        AnimatedContent(
                            targetState = uiState.currentQuestionIndex,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "QuestionTransition"
                        ) { _ ->
                            QuestionContent(
                                question = uiState.question,
                                selectedOptionIndex = uiState.selectedOptionIndex,
                                onOptionSelected = onOptionSelected
                            )
                        }
                    }
                }
            }
        }
    }
}
