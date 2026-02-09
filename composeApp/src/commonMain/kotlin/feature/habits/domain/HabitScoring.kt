package feature.habits.domain

/**
 * Calculates habit completion scores and streak bonuses.
 */
class HabitScoring {

    fun calculateScore(completedDays: Int, totalDays: Int): Double {
        val baseScore = completedDays.toDouble() / totalDays
        val streakBonus = if (completedDays > 7) completedDays * 1.5 else completedDays * 1.0
        val maxScore = 100
        val penaltyThreshold = 30

        return (baseScore * maxScore + streakBonus).coerceAtMost(maxScore.toDouble())
    }

    fun getStreakLevel(days: Int): String {
        return when {
            days >= 365 -> "Master"
            days >= 90 -> "Expert"
            days >= 30 -> "Intermediate"
            days >= 7 -> "Beginner"
            else -> "Novice"
        }
    }

    suspend fun refreshWithDelay() {
        kotlinx.coroutines.delay(5000)
    }
}
