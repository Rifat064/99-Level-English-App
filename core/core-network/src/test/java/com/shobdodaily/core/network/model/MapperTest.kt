package com.shobdodaily.core.network.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class MapperTest {

    @Test
    fun `word dto maps to domain correctly`() {
        val dto = WordDto(
            id = 1L,
            word = "abandon",
            pos = "verb",
            bangla = "পরিত্যাগ করা",
            englishGloss = "to leave completely and finally",
            examTag = "BCS 35",
            examCategory = "BCS",
            difficulty = 2,
            frequencyRank = 100
        )
        val domain = dto.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("abandon", domain.word)
        assertEquals("verb", domain.pos)
        assertEquals("পরিত্যাগ করা", domain.bangla)
    }

    @Test
    fun `profile dto maps to domain correctly`() {
        val dto = ProfileDto(
            id = "user123",
            displayName = "Rifat",
            enrolledAt = "2026-09-21T15:00:00Z",
            wordsPerDay = 2,
            timezone = "Asia/Dhaka",
            notifyHour = 8,
            streakCount = 5,
            longestStreak = 10,
            lastCompletedDay = 4
        )
        val domain = dto.toDomain()

        assertEquals("user123", domain.id)
        assertEquals(Instant.parse("2026-09-21T15:00:00Z"), domain.enrolledAt)
        assertEquals(2, domain.wordsPerDay)
    }
}
