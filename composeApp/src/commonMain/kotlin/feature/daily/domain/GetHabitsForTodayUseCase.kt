package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.presentation.models.DailyHabit
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.tracker.data.TrackerDao
import kotlinx.datetime.*

class GetHabitsForTodayUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao,
    private val trackerDao: TrackerDao
) {

    suspend fun execute(date: LocalDate): List<DailyHabit> {
        val currentDay = date.dayOfWeek.ordinal

        val dailyEntries = dailyDao.getAll()
            .filter {
                val timestamp = LocalDate.parse(it.timestamp)
                date.compareTo(timestamp) == 0 && it.isChecked
            }
            .map { it.habitId }
            
        val habits = habitDao.getAll()
            .filter {
                val cleanDaysToCheck = it.daysToCheck.replace("[", "").replace("]", "")
                val habitDays = cleanDaysToCheck.split(",").map { day -> day.trim().toInt() }
                val startDate = LocalDate.parse(it.startDate)
                val endDate = LocalDate.parse(it.endDate)
                
                habitDays.contains(currentDay) && 
                it.type == HabitType.REGULAR &&
                (date.compareTo(startDate) >= 0) && 
                (date.compareTo(endDate) <= 0)
            }
            .map { habit ->
                DailyHabit(
                    id = habit.id,
                    title = habit.title,
                    isChecked = dailyEntries.contains(habit.id),
                    type = HabitType.REGULAR
                )
            }
        
        return habits
    }
}