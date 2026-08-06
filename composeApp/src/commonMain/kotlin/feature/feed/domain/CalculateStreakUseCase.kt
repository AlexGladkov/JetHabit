package feature.feed.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.tracker.data.TrackerDao
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

class CalculateStreakUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao,
    private val trackerDao: TrackerDao
) {

    suspend fun execute(habitId: String, date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())): Int {
        val habit = habitDao.getAll().firstOrNull { it.id == habitId } ?: return 0

        // Parse scheduled days from daysToCheck string (e.g., "[0,1,2,3,4]")
        val scheduledDays = parseDaysToCheck(habit.daysToCheck)

        if (scheduledDays.isEmpty()) {
            return 0
        }

        // Get all completion records for this habit
        val completionRecords = when (habit.type) {
            HabitType.REGULAR -> {
                dailyDao.getAll()
                    .filter { it.habitId == habitId && it.isChecked }
                    .map { it.timestamp }
                    .toSet()
            }
            HabitType.TRACKER -> {
                trackerDao.getAll()
                    .filter { it.habitId == habitId }
                    .map { it.timestamp }
                    .toSet()
            }
        }

        // Walk backwards from the given date through only scheduled days
        var streakCount = 0
        var currentDate = date

        while (true) {
            val dayOfWeek = currentDate.dayOfWeek.ordinal // 0 = Monday, 6 = Sunday

            // Check if this day is scheduled
            if (scheduledDays.contains(dayOfWeek)) {
                val dateString = currentDate.toString()

                // Check if this scheduled day has a completion record
                if (completionRecords.contains(dateString)) {
                    streakCount++
                } else {
                    // Streak broken - stop counting
                    break
                }
            }

            // Move to the previous day
            currentDate = currentDate.minus(1, DateTimeUnit.DAY)

            // Safety limit: don't go back more than 365 days
            if (streakCount > 365) {
                break
            }
        }

        return streakCount
    }

    private fun parseDaysToCheck(daysToCheckString: String): Set<Int> {
        return try {
            daysToCheckString
                .trim('[', ']')
                .split(',')
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
}
