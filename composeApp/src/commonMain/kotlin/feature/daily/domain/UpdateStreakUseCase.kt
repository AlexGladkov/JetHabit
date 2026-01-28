package feature.daily.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class UpdateStreakUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {

    suspend fun execute(habitId: String) {
        // Get current habit data
        val habit = habitDao.getHabitWith(habitId)

        // Calculate current streak
        val currentStreak = calculateStreakUseCase.execute(habitId)

        // Determine longest streak (never decrease it)
        val longestStreak = maxOf(currentStreak, habit.longestStreak)

        // Get the last completed date
        val completedDates = dailyDao.getCompletedDatesForHabit(habitId)
        val lastCompletedDate = completedDates.firstOrNull() // Already sorted DESC

        // Update the database
        habitDao.updateStreakInfo(
            habitId = habitId,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastCompletedDate = lastCompletedDate
        )
    }
}
