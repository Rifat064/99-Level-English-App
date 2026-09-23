package com.shobdodaily.core.network.repository

import com.shobdodaily.core.database.dao.CardDao
import com.shobdodaily.core.database.dao.WordDao
import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.Word
import com.shobdodaily.core.model.repository.QuizRepository
import javax.inject.Inject

import com.shobdodaily.core.database.dao.QuizAttemptDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstQuizRepository @Inject constructor(
    private val cardDao: CardDao,
    private val wordDao: WordDao,
    private val quizAttemptDao: QuizAttemptDao
) : QuizRepository {

    override suspend fun getCardsForWeek(weekIndex: Int): List<Card> {
        val startDay = (weekIndex - 1) * 7 + 1
        val endDay = weekIndex * 7
        return cardDao.getCardsForWeek(startDay, endDay).map {
            Card(
                id = it.id,
                dayIndex = it.dayIndex,
                season = it.season,
                wordAId = it.wordAId ?: 0L,
                wordBId = it.wordBId ?: 0L,
                sentenceEn = it.sentenceEn,
                sentenceBn = it.sentenceBn,
                bubbleText = it.bubbleText,
                imagePath = it.imagePath,
                imageBlurhash = it.imageBlurhash,
                status = it.status
            )
        }
    }

    override suspend fun getFullWordPool(): List<Word> {
        return wordDao.getAllWords().map {
            Word(
                id = it.id,
                word = it.word,
                pos = it.pos,
                bangla = it.bangla,
                englishGloss = it.englishGloss,
                examTag = it.examTag,
                examCategory = it.examCategory,
                difficulty = it.difficulty,
                frequencyRank = it.frequencyRank
            )
        }
    }

    override suspend fun saveQuizAttempt(attempt: com.shobdodaily.core.model.QuizAttempt) {
        val entity = com.shobdodaily.core.database.model.QuizAttemptEntity(
            id = attempt.id,
            userId = attempt.userId,
            weekIndex = attempt.weekIndex,
            score = attempt.score,
            total = attempt.total,
            answers = attempt.answers,
            takenAt = attempt.takenAt?.toString()
        )
        quizAttemptDao.insertAttempt(entity)
    }

    override fun observeQuizAttempt(userId: String, weekIndex: Int): Flow<com.shobdodaily.core.model.QuizAttempt?> {
        return quizAttemptDao.observeQuizAttempt(userId, weekIndex).map { entity ->
            if (entity == null) null
            else com.shobdodaily.core.model.QuizAttempt(
                id = entity.id,
                userId = entity.userId,
                weekIndex = entity.weekIndex,
                score = entity.score,
                total = entity.total,
                answers = entity.answers,
                takenAt = entity.takenAt?.let { java.time.Instant.parse(it) }
            )
        }
    }

    override suspend fun getAllAttempts(userId: String): List<com.shobdodaily.core.model.QuizAttempt> {
        return quizAttemptDao.getAllAttempts(userId).map { entity ->
            com.shobdodaily.core.model.QuizAttempt(
                id = entity.id,
                userId = entity.userId,
                weekIndex = entity.weekIndex,
                score = entity.score,
                total = entity.total,
                answers = entity.answers,
                takenAt = entity.takenAt?.let { java.time.Instant.parse(it) }
            )
        }
    }
}
