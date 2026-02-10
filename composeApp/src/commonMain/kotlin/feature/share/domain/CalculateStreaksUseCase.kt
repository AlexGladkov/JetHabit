package feature.share.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Use case to calculate the current streak for a given habit.
 * A streak is the number of consecutive days (counting backwards from today)
 * that the habit was completed, considering only the days that are in the habit's daysToCheck.
 */
class CalculateStreaksUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao
) {
    /**
     * Calculates the current streak for a habit.
     *
     * @param habitId The ID of the habit
     * @return The number of consecutive days the habit has been checked, or 0 if not applicable
     */
    suspend fun execute(habitId: String): Int {
        try {
            val habit = habitDao.getHabitWith(habitId)

            // Only calculate streaks for REGULAR habits (tracker habits don't have boolean streaks)
            if (habit.type != HabitType.REGULAR) {
                return 0
            }

            // Parse the days to check (format: "[0,1,2,3,4,5,6]" where 0=Monday, 6=Sunday)
            val cleanDaysToCheck = habit.daysToCheck.replace("[", "").replace("]", "")
            val daysToCheck = cleanDaysToCheck
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()

            if (daysToCheck.isEmpty()) {
                return 0
            }

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val startDate = LocalDate.parse(habit.startDate)

            // Don't calculate streak if the habit hasn't started yet
            if (today < startDate) {
                return 0
            }

            var currentDate = today
            var streak = 0

            // Walk backwards from today, checking consecutive days
            while (currentDate >= startDate) {
                // Get day of week (0 = Monday, 6 = Sunday)
                val dayOfWeek = currentDate.dayOfWeek.ordinal

                // Only check days that are in the habit's daysToCheck
                if (daysToCheck.contains(dayOfWeek)) {
                    val isChecked = dailyDao.isHabitChecked(habitId, currentDate.toString())

                    if (isChecked) {
                        streak++
                    } else {
                        // Streak broken - stop counting
                        break
                    }
                }

                // Move to previous day
                currentDate = currentDate.minusDays(1)
            }

            return streak
        } catch (e: Exception) {
            // If habit not found or any error, return 0
            return 0
        }
    }

    private fun LocalDate.minusDays(days: Int): LocalDate {
        // Calculate previous date by subtracting days
        val epochDays = this.toEpochDays() - days
        return LocalDate.fromEpochDays(epochDays)
    }
}
