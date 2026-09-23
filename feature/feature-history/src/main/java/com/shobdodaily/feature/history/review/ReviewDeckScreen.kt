package com.shobdodaily.feature.history.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shobdodaily.feature.history.R
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shobdodaily.core.model.Card

@Composable
fun ReviewDeckRoute(
    onNavigateBack: () -> Unit,
    viewModel: ReviewDeckViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    ReviewDeckScreen(
        uiState = uiState,
        onSubmitRating = viewModel::submitRating,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDeckScreen(
    uiState: ReviewUiState,
    onSubmitRating: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.review_deck)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←") // Or Icon
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (uiState) {
                is ReviewUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ReviewUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is ReviewUiState.Finished -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.all_done_for_today), style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text(stringResource(R.string.go_back))
                        }
                    }
                }
                is ReviewUiState.ActiveReview -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.cards_remaining, uiState.remainingCount))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Card Display
                        ReviewCardView(
                            payload = uiState.payload,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        var showAnswer by remember { mutableStateOf(false) }
                        
                        if (!showAnswer) {
                            Button(
                                onClick = { showAnswer = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.show_answer))
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { 
                                        showAnswer = false
                                        onSubmitRating(3) // Hard
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(stringResource(R.string.hard_1d))
                                }
                                
                                Button(
                                    onClick = { 
                                        showAnswer = false
                                        onSubmitRating(2) // Medium
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text(stringResource(R.string.good_3d))
                                }
                                
                                Button(
                                    onClick = { 
                                        showAnswer = false
                                        onSubmitRating(1) // Easy
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(stringResource(R.string.easy_7d))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCardView(payload: com.shobdodaily.core.model.DailyCardPayload, modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display words
            Text(
                text = "${payload.wordA.word} / ${payload.wordB.word}",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${payload.wordA.bangla} / ${payload.wordB.bangla}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = payload.card.sentenceEn,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = payload.card.sentenceBn,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
