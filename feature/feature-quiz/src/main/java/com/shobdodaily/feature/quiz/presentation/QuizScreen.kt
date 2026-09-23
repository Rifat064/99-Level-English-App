package com.shobdodaily.feature.quiz.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.shobdodaily.feature.quiz.R

@Composable
fun QuizRoute(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuizScreen(
        uiState = uiState,
        onOptionSelected = viewModel::selectOption,
        onSubmitAnswer = viewModel::submitAnswer,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.weekly_quiz)) },
                // Notice there is no back button on the Quiz itself to prevent back-editing!
                // But we can allow exiting the quiz entirely (though not navigating to previous questions).
            )
        },
        bottomBar = {
            if (uiState is QuizUiState.ActiveQuestion) {
                Button(
                    onClick = onSubmitAnswer,
                    enabled = uiState.selectedOptionIndex != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) stringResource(R.string.finish_quiz) else stringResource(R.string.next_question))
                }
            } else if (uiState is QuizUiState.Finished) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.return_home))
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is QuizUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is QuizUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is QuizUiState.Finished -> {
                    QuizResultsScreen(
                        score = uiState.score,
                        totalQuestions = uiState.totalQuestions,
                        missedWords = uiState.missedWords
                    )
                }
                is QuizUiState.ActiveQuestion -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LinearProgressIndicator(
                            progress = { uiState.currentQuestionIndex / uiState.totalQuestions.toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        
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
