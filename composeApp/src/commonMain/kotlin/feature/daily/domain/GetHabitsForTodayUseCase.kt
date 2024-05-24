package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.presentation.models.DailyHabit
import feature.habits.data.HabitDao
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class GetHabitsForTodayUseCase(
    private val json: Json,
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao
) {

    suspend fun execute(date: LocalDate): List<DailyHabit> {
        val currentDay = date.dayOfWeek.ordinal
        
        val habits = habitDao.getAll()
            .filter {
                val habitDays = json.decodeFromString<List<Int>>(it.daysToCheck)
                habitDays.contains(currentDay)
            }
            .map {
                DailyHabit(
                    id = it.id,
                    title = it.title,
                    isChecked = true
                )
            }
        
        return habits
    }
}