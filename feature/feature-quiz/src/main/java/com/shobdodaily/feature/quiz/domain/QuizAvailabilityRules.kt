package com.shobdodaily.feature.quiz.domain

enum class QuizState {
    AVAILABLE,
    LOCKED_FUTURE,
    LOCKED_PREMIUM,
    COUNTDOWN_SUNDAY
}

class QuizAvailabilityRules {
    /**
     * @param targetWeekIndex The 1-based index of the quiz week (e.g. 1 for the first 7 days)
     * @param currentDayIndex The 1-based index of the user's current day since enrollment
     * @param isPremium True if the user has an active premium subscription
     */
    fun getQuizState(targetWeekIndex: Int, currentDayIndex: Int, isPremium: Boolean): QuizState {
        require(targetWeekIndex > 0) { "targetWeekIndex must be >= 1" }
        require(currentDayIndex > 0) { "currentDayIndex must be >= 1" }

        val currentWeek = (currentDayIndex - 1) / 7 + 1
        val unlockedWeeks = currentWeek - 1

        val isSunday = currentDayIndex % 7 == 0
        val isTargetWeekCurrentWeek = targetWeekIndex == currentWeek

        return when {
            targetWeekIndex > unlockedWeeks -> {
                if (isSunday && isTargetWeekCurrentWeek) {
                    QuizState.COUNTDOWN_SUNDAY
                } else {
                    QuizState.LOCKED_FUTURE
                }
            }
            !isPremium && targetWeekIndex < unlockedWeeks -> {
                QuizState.LOCKED_PREMIUM
            }
            else -> {
                QuizState.AVAILABLE
            }
        }
    }
}
