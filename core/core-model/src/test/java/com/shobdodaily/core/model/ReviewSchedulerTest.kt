package com.shobdodaily.core.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class ReviewSchedulerTest {

    @Test
    fun `test bookmarked card with no rating is due tomorrow`() {
        val now = Instant.now()
        val seenAt = now.minus(24, ChronoUnit.HOURS).toString()
        
        val progress = UserProgress(
            userId = "1", cardId = 1, seenAt = seenAt, completed = true, bookmarked = true, selfRating = null
        )
        
        val dueCards = ReviewScheduler.getDueCards(listOf(progress), now)
        assertEquals(1, dueCards.size)
    }

    @Test
    fun `test bookmarked card with no rating is NOT due right away`() {
        val now = Instant.now()
        val seenAt = now.minus(12, ChronoUnit.HOURS).toString() // only 12 hours ago
        
        val progress = UserProgress(
            userId = "1", cardId = 1, seenAt = seenAt, completed = true, bookmarked = true, selfRating = null
        )
        
        val dueCards = ReviewScheduler.getDueCards(listOf(progress), now)
        assertEquals(0, dueCards.size)
    }

    @Test
    fun `test self rating 3 (Hard) is due after 1 day`() {
        val now = Instant.now()
        val seenAt = now.minus(25, ChronoUnit.HOURS).toString() 
        
        val progress = UserProgress(
            userId = "1", cardId = 1, seenAt = seenAt, completed = true, bookmarked = false, selfRating = 3
        )
        
        val dueCards = ReviewScheduler.getDueCards(listOf(progress), now)
        assertEquals(1, dueCards.size)
    }

    @Test
    fun `test self rating 2 (Medium) is due after 3 days`() {
        val now = Instant.now()
        
        // 2 days ago -> NOT due
        val progressNotDue = UserProgress(
            userId = "1", cardId = 1, seenAt = now.minus(2, ChronoUnit.DAYS).toString(), 
            completed = true, bookmarked = false, selfRating = 2
        )
        assertEquals(0, ReviewScheduler.getDueCards(listOf(progressNotDue), now).size)

        // 4 days ago -> Due
        val progressDue = UserProgress(
            userId = "1", cardId = 2, seenAt = now.minus(4, ChronoUnit.DAYS).toString(), 
            completed = true, bookmarked = false, selfRating = 2
        )
        assertEquals(1, ReviewScheduler.getDueCards(listOf(progressDue), now).size)
    }

    @Test
    fun `test self rating 1 (Easy) is due after 7 days`() {
        val now = Instant.now()
        
        // 6 days ago -> NOT due
        val progressNotDue = UserProgress(
            userId = "1", cardId = 1, seenAt = now.minus(6, ChronoUnit.DAYS).toString(), 
            completed = true, bookmarked = false, selfRating = 1
        )
        assertEquals(0, ReviewScheduler.getDueCards(listOf(progressNotDue), now).size)

        // 8 days ago -> Due
        val progressDue = UserProgress(
            userId = "1", cardId = 2, seenAt = now.minus(8, ChronoUnit.DAYS).toString(), 
            completed = true, bookmarked = false, selfRating = 1
        )
        assertEquals(1, ReviewScheduler.getDueCards(listOf(progressDue), now).size)
    }

    @Test
    fun `test card with no rating and not bookmarked is never due`() {
        val now = Instant.now()
        val seenAt = now.minus(100, ChronoUnit.DAYS).toString() 
        
        val progress = UserProgress(
            userId = "1", cardId = 1, seenAt = seenAt, completed = true, bookmarked = false, selfRating = null
        )
        
        val dueCards = ReviewScheduler.getDueCards(listOf(progress), now)
        assertEquals(0, dueCards.size)
    }
}
