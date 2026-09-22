package com.shobdodaily.feature.quiz.domain

import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.Word
import org.junit.Assert.*
import org.junit.Test

class QuizGeneratorTest {

    private fun mockWord(id: Long, cat: String = "BCS", diff: Int = 3, pos: String = "noun"): Word {
        return Word(
            id = id,
            word = "word$id",
            pos = pos,
            bangla = "bangla$id",
            englishGloss = "gloss$id",
            examTag = "$cat 35",
            examCategory = cat,
            difficulty = diff,
            frequencyRank = id.toInt()
        )
    }

    private fun mockCard(id: Long, wordAId: Long, wordBId: Long): Card {
        return Card(
            id = id,
            dayIndex = id.toInt(),
            season = 1,
            wordAId = wordAId,
            wordBId = wordBId,
            sentenceEn = "This is a sentence with word$wordAId and word$wordBId.",
            sentenceBn = "This is bangla sentence.",
            bubbleText = null,
            imagePath = "path/to/image",
            imageBlurhash = null,
            status = "published"
        )
    }

    @Test
    fun `test generator produces exactly 12 questions and covers all 14 words`() {
        val fullPool = (1L..100L).map { mockWord(it, cat = if (it % 2 == 0L) "BCS" else "BANK") }
        
        val weeklyWords = fullPool.take(14)
        val weeklyCards = (0..6).map { 
            mockCard(it.toLong(), weeklyWords[it * 2].id, weeklyWords[it * 2 + 1].id) 
        }

        val generator = QuizGenerator()
        val quiz = generator.generateQuiz("user123", 1, weeklyCards, fullPool)

        assertEquals("Quiz should have 12 questions", 12, quiz.questions.size)

        val coveredWords = mutableSetOf<Long>()
        
        quiz.questions.forEach { q ->
            coveredWords.add(q.targetWord.id)
            if (q is Question.SentenceRecall) {
                coveredWords.add(q.secondaryWord.id)
            }
        }

        assertEquals("All 14 words should be covered", 14, coveredWords.size)
    }

    @Test
    fun `test distractor rules`() {
        val fullPool = (1L..100L).map { mockWord(it, diff = (it % 5).toInt() + 1) }
        val weeklyWords = fullPool.take(14)
        val weeklyCards = (0..6).map { 
            mockCard(it.toLong(), weeklyWords[it * 2].id, weeklyWords[it * 2 + 1].id) 
        }

        val generator = QuizGenerator()
        val quiz = generator.generateQuiz("user123", 1, weeklyCards, fullPool)

        quiz.questions.forEach { q ->
            assertEquals("Each question should have exactly 4 options", 4, q.options.size)
            val distinctOptions = q.options.distinct()
            assertEquals("All options should be distinct", 4, distinctOptions.size)

            assertTrue("Correct answer should be in options", q.options.contains(q.correctAnswer))
            
            // Check that distractors don't equal correct answer
            val distractors = q.options.filter { it != q.correctAnswer }
            assertEquals("There should be exactly 3 distractors", 3, distractors.size)
        }
    }

    @Test
    fun `test seed reproducibility`() {
        val fullPool = (1L..100L).map { mockWord(it) }
        val weeklyWords = fullPool.take(14)
        val weeklyCards = (0..6).map { 
            mockCard(it.toLong(), weeklyWords[it * 2].id, weeklyWords[it * 2 + 1].id) 
        }

        val generator = QuizGenerator()
        val quiz1 = generator.generateQuiz("user123", 1, weeklyCards, fullPool)
        val quiz2 = generator.generateQuiz("user123", 1, weeklyCards, fullPool)
        val quiz3 = generator.generateQuiz("user124", 1, weeklyCards, fullPool)
        
        // Assert identical questions and options
        assertEquals(quiz1.questions, quiz2.questions)
        
        // Assert different seed produces different output (with high probability)
        assertNotEquals(quiz1.questions, quiz3.questions)
    }
}
