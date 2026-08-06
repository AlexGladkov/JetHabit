package feature.share.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.share.domain.models.ShareCardData
import feature.share.domain.models.ShareableHabit
import kotlinx.datetime.*

/**
 * Use case to get the top habits for sharing.
 * Returns the top 5 habits sorted by current streak (descending),
 * along with their completion rates.
 */
class GetTopHabitsForSharingUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao,
    private val calculateStreaksUseCase: CalculateStreaksUseCase
) {
    /**
     * Gets the top habits to display on the share card.
     *
     * @return ShareCardData containing top 5 habits and total habit count
     */
    suspend fun execute(): ShareCardData {
        try {
            val allHabits = habitDao.getAll()

            // Filter to only REGULAR type habits (tracker habits don't have streaks)
            val regularHabits = allHabits.filter { it.type == HabitType.REGULAR }

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            // Calculate streak and completion rate for each habit
            val shareableHabits = regularHabits.mapNotNull { habit ->
                try {
                    val streak = calculateStreaksUseCase.execute(habit.id)
                    val completionRate = calculateCompletionRate(habit.id, habit.startDate, habit.daysToCheck, today)

                    ShareableHabit(
                        title = habit.title,
                        currentStreak = streak,
                        completionRate = completionRate
                    )
                } catch (e: Exception) {
                    // Skip habits that have errors
                    null
                }
            }

            // Sort by streak descending, then by completion rate descending
            val sortedHabits = shareableHabits.sortedWith(
                compareByDescending<ShareableHabit> { it.currentStreak }
                    .thenByDescending { it.completionRate }
            )

            // Take top 5
            val topHabits = sortedHabits.take(5)

            return ShareCardData(
                habits = topHabits,
                totalHabitCount = regularHabits.size
            )
        } catch (e: Exception) {
            // Return empty data on error
            return ShareCardData(
                habits = emptyList(),
                totalHabitCount = 0
            )
        }
    }

    /**
     * Calculates the completion rate for a habit.
     * Similar logic to StatisticsViewModel.
     */
    private suspend fun calculateCompletionRate(
        habitId: String,
        startDateStr: String,
        daysToCheckStr: String,
        today: LocalDate
    ): Float {
        try {
            val startDate = if (startDateStr.isBlank()) {
                today
            } else {
                LocalDate.parse(startDateStr)
            }

            // Parse days to check
            val cleanDaysToCheck = daysToCheckStr.replace("[", "").replace("]", "")
            val daysToCheck = cleanDaysToCheck.split(",").mapNotNull { it.trim().toIntOrNull() }

            if (daysToCheck.isEmpty()) {
                return 0f
            }

            var currentDate = startDate
            var trackedCount = 0
            var totalDays = 0

            // Count from start date to today
            while (currentDate <= today) {
                // Only include days that are in daysToCheck
                if (daysToCheck.contains(currentDate.dayOfWeek.ordinal)) {
                    totalDays++
                    val isChecked = dailyDao.isHabitChecked(habitId, currentDate.toString())
                    if (isChecked) trackedCount++
                }
                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            }

            return if (totalDays > 0) {
                trackedCount.toFloat() / totalDays
            } else {
                0f
            }
        } catch (e: Exception) {
            return 0f
        }
    }
}
