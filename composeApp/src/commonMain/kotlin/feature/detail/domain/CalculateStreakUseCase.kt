package feature.detail.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class CalculateStreakUseCase(
    private val dailyDao: DailyDao,
    private val habitDao: HabitDao
) {
    suspend fun execute(habitId: String): Int {
        val habit = habitDao.getHabitWith(habitId)
        val allDailyRecords = dailyDao.getAll()
            .filter { it.habitId == habitId && it.isChecked }
            .sortedByDescending { it.timestamp }

        if (allDailyRecords.isEmpty()) {
            return 0
        }

        // Parse daysToCheck from habit
        val daysToCheck = habit.daysToCheck
            .trim('[', ']')
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }
            .toSet()

        // Get today's date
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        // Create a set of checked dates for fast lookup
        val checkedDates = allDailyRecords.map {
            LocalDate.parse(it.timestamp)
        }.toSet()

        // Start counting from today backwards
        var currentDate = today
        var streak = 0

        // Check if we should start from today or the most recent checked date
        if (!checkedDates.contains(today)) {
            // Find the most recent checked date
            val mostRecentChecked = checkedDates.maxOrNull()
            if (mostRecentChecked == null || mostRecentChecked < today.minus(1, DateTimeUnit.DAY)) {
                // If no recent check or gap exists, streak is broken
                return 0
            }
            currentDate = mostRecentChecked
        }

        // Count consecutive days backwards
        while (true) {
            // Check if this day should be tracked
            val dayOfWeek = currentDate.dayOfWeek.ordinal // Monday = 0, Sunday = 6

            if (daysToCheck.isEmpty() || daysToCheck.contains(dayOfWeek)) {
                // This day should be tracked
                if (checkedDates.contains(currentDate)) {
                    streak++
                } else {
                    // Streak broken
                    break
                }
            }

            // Move to previous day
            currentDate = currentDate.minus(1, DateTimeUnit.DAY)

            // Safety check: don't go back more than a year
            if (currentDate < today.minus(365, DateTimeUnit.DAY)) {
                break
            }
        }

        return streak
    }

    suspend fun calculateCompletionRate(habitId: String): Int {
        val habit = habitDao.getHabitWith(habitId)
        val startDate = LocalDate.parse(habit.startDate)
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        val allDailyRecords = dailyDao.getAll()
            .filter { it.habitId == habitId }

        val checkedDates = allDailyRecords
            .filter { it.isChecked }
            .map { LocalDate.parse(it.timestamp) }
            .toSet()

        // Parse daysToCheck
        val daysToCheck = habit.daysToCheck
            .trim('[', ']')
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }
            .toSet()

        // Count expected days
        var currentDate = startDate
        var expectedDays = 0

        while (currentDate <= today) {
            val dayOfWeek = currentDate.dayOfWeek.ordinal
            if (daysToCheck.isEmpty() || daysToCheck.contains(dayOfWeek)) {
                expectedDays++
            }
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        if (expectedDays == 0) {
            return 0
        }

        val completedDays = checkedDates.size
        return ((completedDays.toDouble() / expectedDays.toDouble()) * 100).toInt().coerceIn(0, 100)
    }
}
