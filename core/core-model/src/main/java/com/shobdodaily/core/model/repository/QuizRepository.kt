package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.Word

interface QuizRepository {
    suspend fun getCardsForWeek(weekIndex: Int): List<Card>
    suspend fun getFullWordPool(): List<Word>
    suspend fun saveQuizAttempt(attempt: com.shobdodaily.core.model.QuizAttempt)
    fun observeQuizAttempt(userId: String, weekIndex: Int): kotlinx.coroutines.flow.Flow<com.shobdodaily.core.model.QuizAttempt?>
}
