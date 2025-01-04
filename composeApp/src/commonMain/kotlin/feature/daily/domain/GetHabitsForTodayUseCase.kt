package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.presentation.models.DailyHabit
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.tracker.data.TrackerDao
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

class GetHabitsForTodayUseCase(
    private val json: Json,
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
                val habitDays = json.decodeFromString<List<Int>>(it.daysToCheck)
                habitDays.contains(currentDay)
            }
            .map { habit ->
                when (habit.type) {
                    HabitType.REGULAR -> DailyHabit(
                        id = habit.id,
                        title = habit.title,
                        isChecked = dailyEntries.contains(habit.id),
                        type = HabitType.REGULAR
                    )
                    HabitType.TRACKER -> {
                        val trackerValue = trackerDao.getValueForDate(habit.id, date.toString())
                        DailyHabit(
                            id = habit.id,
                            title = habit.title,
                            isChecked = trackerValue != null,
                            type = HabitType.TRACKER,
                            value = trackerValue?.value
                        )
                    }
                }
            }
        
        return habits
    }
}