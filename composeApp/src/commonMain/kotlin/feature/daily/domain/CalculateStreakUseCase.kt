package feature.daily.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json

class CalculateStreakUseCase(
    private val dailyDao: DailyDao,
    private val habitDao: HabitDao
) {

    suspend fun execute(habitId: String, targetDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())): Int {
        val habit = habitDao.getHabitWith(habitId)

        // Get all completed dates for this habit
        val completedDates = dailyDao.getCompletedDatesForHabit(habitId)
            .map { LocalDate.parse(it) }
            .sortedDescending()

        if (completedDates.isEmpty()) {
            return 0
        }

        // Parse the daysToCheck array (e.g., "[0,1,2,3,4,5,6]")
        val daysToCheck = try {
            Json.decodeFromString<List<Int>>(habit.daysToCheck)
        } catch (e: Exception) {
            emptyList()
        }

        if (daysToCheck.isEmpty()) {
            return 0
        }

        var streak = 0
        var currentCheckDate = targetDate
        var isFirstIteration = true

        // Start from the target date and go backwards
        while (true) {
            // Check if this date is a scheduled day for the habit
            val dayOfWeek = currentCheckDate.dayOfWeek.ordinal // 0 = Monday, 6 = Sunday

            if (daysToCheck.contains(dayOfWeek)) {
                // This is a scheduled day - check if it was completed
                if (completedDates.contains(currentCheckDate)) {
                    streak++
                } else {
                    // If this is the target date (first iteration) and it's not completed yet,
                    // skip it and continue counting backwards (the day isn't over yet)
                    if (isFirstIteration) {
                        // Don't break the streak, just skip today and continue
                    } else {
                        // Scheduled day was missed - streak is broken
                        break
                    }
                }
                isFirstIteration = false
            }

            // Move to previous day
            currentCheckDate = currentCheckDate.minus(1, kotlinx.datetime.DateTimeUnit.DAY)

            // Don't go before the habit's start date
            val startDate = LocalDate.parse(habit.startDate)
            if (currentCheckDate < startDate) {
                break
            }
        }

        return streak
    }
}
