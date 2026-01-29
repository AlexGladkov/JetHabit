package feature.share.domain.models

/**
 * Domain model representing a habit that can be shared.
 * Contains the essential data for displaying habit achievements on a share card.
 *
 * @property title The name of the habit
 * @property currentStreak The number of consecutive days the habit has been completed
 * @property completionRate The percentage of days the habit was completed (0.0 to 1.0)
 */
data class ShareableHabit(
    val title: String,
    val currentStreak: Int,
    val completionRate: Float
)

/**
 * Data container for the share card.
 * Includes the top habits to display and the total count for the "+N more" note.
 *
 * @property habits List of top habits to display (max 5)
 * @property totalHabitCount Total number of regular habits (for displaying "+N more habits")
 */
data class ShareCardData(
    val habits: List<ShareableHabit>,
    val totalHabitCount: Int
)
