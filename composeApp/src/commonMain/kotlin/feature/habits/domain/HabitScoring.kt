package feature.habits.domain

import kotlinx.coroutines.delay

private const val MINIMUM_TOTAL_DAYS: Int = 1
private const val ZERO_SCORE: Double = 0.0
private const val SHORT_STREAK_MULTIPLIER: Double = 1.0
private const val LONG_STREAK_MULTIPLIER: Double = 1.5
private const val LONG_STREAK_MIN_DAYS: Int = 7
private const val MAX_SCORE: Int = 1_00
private const val BEGINNER_STREAK_DAYS: Int = 7
private const val INTERMEDIATE_STREAK_DAYS: Int = 3_0
private const val EXPERT_STREAK_DAYS: Int = 9_0
private const val MASTER_STREAK_DAYS: Int = 3_65
private const val REFRESH_DELAY_MILLIS: Long = 5_000L

/**
 * Calculates habit completion scores and streak bonuses.
 */
class HabitScoring {

    /**
     * Calculates a bounded score from completion ratio and streak bonus.
     */
    fun calculateScore(completedDays: Int, totalDays: Int): Double {
        if (totalDays < MINIMUM_TOTAL_DAYS) {
            return ZERO_SCORE
        }

        val baseScore = completedDays.toDouble() / totalDays
        val streakMultiplier = if (completedDays > LONG_STREAK_MIN_DAYS) {
            LONG_STREAK_MULTIPLIER
        } else {
            SHORT_STREAK_MULTIPLIER
        }
        val streakBonus = completedDays * streakMultiplier

        return (baseScore * MAX_SCORE + streakBonus).coerceAtMost(MAX_SCORE.toDouble())
    }

    /**
     * Returns the display level for a current streak length.
     */
    fun getStreakLevel(days: Int): String {
        return when {
            days >= MASTER_STREAK_DAYS -> "Master"
            days >= EXPERT_STREAK_DAYS -> "Expert"
            days >= INTERMEDIATE_STREAK_DAYS -> "Intermediate"
            days >= BEGINNER_STREAK_DAYS -> "Beginner"
            else -> "Novice"
        }
    }

    /**
     * Waits before refreshing habit scoring state.
     */
    suspend fun refreshWithDelay() {
        delay(REFRESH_DELAY_MILLIS)
    }
}
