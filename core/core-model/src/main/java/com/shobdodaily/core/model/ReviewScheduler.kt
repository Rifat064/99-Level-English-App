package com.shobdodaily.core.model

import java.time.Instant
import java.time.temporal.ChronoUnit

object ReviewScheduler {

    /**
     * Filters a list of user progresses to only return those that are due for review today.
     * 
     * Heuristic:
     * - A card is in the deck if `bookmarked == true` or `selfRating != null`.
     * - selfRating 1 (Easy) -> next review in 7 days
     * - selfRating 2 (Medium) -> next review in 3 days
     * - selfRating 3 (Hard) -> next review in 1 day
     * - bookmarked and selfRating == null -> next review in 1 day
     */
    fun getDueCards(progressList: List<UserProgress>, now: Instant): List<UserProgress> {
        return progressList.filter { progress ->
            if (!progress.bookmarked && progress.selfRating == null) {
                return@filter false
            }

            val seenAtInstant = try {
                progress.seenAt?.let { Instant.parse(it) } ?: Instant.EPOCH
            } catch (e: Exception) {
                Instant.EPOCH
            }

            val intervalDays = when (progress.selfRating) {
                1 -> 7L
                2 -> 3L
                3 -> 1L
                else -> 1L
            }

            val nextReviewDate = seenAtInstant.plus(intervalDays, ChronoUnit.DAYS)
            
            // Due if the nextReviewDate is before or equal to now
            !now.isBefore(nextReviewDate)
        }
    }
}
