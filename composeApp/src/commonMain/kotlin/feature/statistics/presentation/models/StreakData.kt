package feature.statistics.presentation.models

/**
 * Data class containing streak information for sharing.
 *
 * @param habitTitle The name of the habit
 * @param streakCount The current consecutive-day streak count
 * @param habitId The unique identifier for the habit
 */
data class StreakData(
    val habitTitle: String,
    val streakCount: Int,
    val habitId: String
)
