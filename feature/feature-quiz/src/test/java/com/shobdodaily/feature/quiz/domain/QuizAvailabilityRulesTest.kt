package com.shobdodaily.feature.quiz.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class QuizAvailabilityRulesTest {

    private val rules = QuizAvailabilityRules()

    @Test
    fun `test week 1 quiz availability during week 1`() {
        // Monday of week 1 (day 1)
        assertEquals(QuizState.LOCKED_FUTURE, rules.getQuizState(1, 1, false))
        assertEquals(QuizState.LOCKED_FUTURE, rules.getQuizState(1, 1, true))

        // Saturday of week 1 (day 6)
        assertEquals(QuizState.LOCKED_FUTURE, rules.getQuizState(1, 6, false))

        // Sunday of week 1 (day 7)
        assertEquals(QuizState.COUNTDOWN_SUNDAY, rules.getQuizState(1, 7, false))
        assertEquals(QuizState.COUNTDOWN_SUNDAY, rules.getQuizState(1, 7, true))
    }

    @Test
    fun `test week 1 quiz availability during week 2`() {
        // Monday of week 2 (day 8)
        // Free user gets the most recent week only (which is week 1)
        assertEquals(QuizState.AVAILABLE, rules.getQuizState(1, 8, false))
        // Premium user gets all unlocked weeks
        assertEquals(QuizState.AVAILABLE, rules.getQuizState(1, 8, true))

        // Sunday of week 2 (day 14)
        assertEquals(QuizState.AVAILABLE, rules.getQuizState(1, 14, false))
    }

    @Test
    fun `test week 2 quiz availability during week 2`() {
        // Monday of week 2 (day 8)
        assertEquals(QuizState.LOCKED_FUTURE, rules.getQuizState(2, 8, false))

        // Sunday of week 2 (day 14)
        assertEquals(QuizState.COUNTDOWN_SUNDAY, rules.getQuizState(2, 14, false))
    }

    @Test
    fun `test week 1 quiz availability during week 3`() {
        // Monday of week 3 (day 15)
        // Week 1 is no longer the most recent unlocked week (week 2 is)
        // So free user is locked out
        assertEquals(QuizState.LOCKED_PREMIUM, rules.getQuizState(1, 15, false))
        // Premium user retains access
        assertEquals(QuizState.AVAILABLE, rules.getQuizState(1, 15, true))
    }
    
    @Test
    fun `test week 2 quiz availability during week 3`() {
        // Monday of week 3 (day 15)
        // Week 2 is the most recent unlocked week
        assertEquals(QuizState.AVAILABLE, rules.getQuizState(2, 15, false))
        assertEquals(QuizState.AVAILABLE, rules.getQuizState(2, 15, true))
    }
}
