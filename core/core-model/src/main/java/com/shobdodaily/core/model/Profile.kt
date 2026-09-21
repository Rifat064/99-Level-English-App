package com.shobdodaily.core.model

import java.time.Instant

data class Profile(
    val id: String,
    val displayName: String?,
    val enrolledAt: Instant,
    val wordsPerDay: Int,
    val timezone: String,
    val notifyHour: Int,
    val streakCount: Int,
    val longestStreak: Int,
    val lastCompletedDay: Int
)
