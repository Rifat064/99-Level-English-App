package com.shobdodaily.feature.quiz.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.core.model.repository.QuizRepository
import com.shobdodaily.feature.quiz.domain.QuizGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository,
    private val profileRepository: ProfileRepository,
    private val progressRepository: com.shobdodaily.core.model.repository.ProgressRepository
) : ViewModel() {

    private val weekIndex: Int = checkNotNull(savedStateHandle["weekIndex"])

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val generator = QuizGenerator()

    // Key for restoring state
    private val KEY_CURRENT_INDEX = "current_question_index"
    private val KEY_SUBMITTED_ANSWERS = "submitted_answers"

    private var generatedQuiz: com.shobdodaily.feature.quiz.domain.Quiz? = null

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            try {
                // 1. Fetch data
                val cards = quizRepository.getCardsForWeek(weekIndex)
                if (cards.size < 7) {
                    _uiState.value = QuizUiState.Error("Not enough cards for this week.")
                    return@launch
                }
                
                val fullWordPool = quizRepository.getFullWordPool()
                
                val profile = profileRepository.getProfile().getOrNull()
                val userId = profile?.id ?: "anonymous"

                // 2. Generate quiz
                generatedQuiz = generator.generateQuiz(userId, weekIndex, cards, fullWordPool)
                
                updateUiStateFromSavedState()
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Failed to load quiz")
            }
        }
    }

    private fun updateUiStateFromSavedState() {
        val quiz = generatedQuiz ?: return
        val currentIndex = savedStateHandle.get<Int>(KEY_CURRENT_INDEX) ?: 0
        val submittedAnswers = savedStateHandle.get<List<Int>>(KEY_SUBMITTED_ANSWERS) ?: emptyList()

        if (currentIndex >= quiz.questions.size) {
            var score = 0
            val missedWords = mutableListOf<com.shobdodaily.core.model.Word>()
            val answersList = mutableListOf<String>()

            for (i in quiz.questions.indices) {
                val question = quiz.questions[i]
                val selectedOptionIndex = submittedAnswers.getOrNull(i) ?: -1
                
                if (selectedOptionIndex >= 0 && selectedOptionIndex < question.options.size) {
                    val selectedText = question.options[selectedOptionIndex]
                    answersList.add(selectedText)
                    if (selectedText == question.correctAnswer) {
                        score++
                    } else {
                        missedWords.add(question.targetWord)
                    }
                } else {
                    answersList.add("")
                    missedWords.add(question.targetWord)
                }
            }

            // Save attempt to repository
            viewModelScope.launch {
                val userId = profileRepository.getProfile().getOrNull()?.id ?: "anonymous"
                quizRepository.saveQuizAttempt(
                    com.shobdodaily.core.model.QuizAttempt(
                        userId = userId,
                        weekIndex = weekIndex,
                        score = score,
                        total = quiz.questions.size,
                        answers = answersList.joinToString(","), // Simple serialization
                        takenAt = java.time.Instant.now()
                    )
                )
                
                // Missed words < 60% rule: Push to spaced-repetition deck
                val threshold = quiz.questions.size * 0.6f
                if (score < threshold) {
                    val distinctMissedWords = missedWords.distinctBy { it.id }
                    distinctMissedWords.forEach { word ->
                        // Hard rating (3) pushes it to tomorrow
                        progressRepository.updateSelfRating(word.id, 3)
                    }
                }
            }

            _uiState.value = QuizUiState.Finished(
                totalQuestions = quiz.questions.size,
                score = score,
                missedWords = missedWords.distinctBy { it.id }
            )
        } else {
            _uiState.value = QuizUiState.ActiveQuestion(
                question = quiz.questions[currentIndex],
                currentQuestionIndex = currentIndex,
                totalQuestions = quiz.questions.size,
                selectedOptionIndex = null
            )
        }
    }

    fun selectOption(optionIndex: Int) {
        val currentState = _uiState.value
        if (currentState is QuizUiState.ActiveQuestion) {
            _uiState.value = currentState.copy(selectedOptionIndex = optionIndex)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState is QuizUiState.ActiveQuestion) {
            val selectedIndex = currentState.selectedOptionIndex ?: return 
            
            // Save answer
            val currentAnswers = savedStateHandle.get<List<Int>>(KEY_SUBMITTED_ANSWERS) ?: emptyList()
            val newAnswers = currentAnswers + selectedIndex
            savedStateHandle[KEY_SUBMITTED_ANSWERS] = newAnswers

            // Advance to next question
            val nextIndex = currentState.currentQuestionIndex + 1
            savedStateHandle[KEY_CURRENT_INDEX] = nextIndex
            
            updateUiStateFromSavedState()
        }
    }
}
