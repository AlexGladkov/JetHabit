package feature.settings.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao

class ClearAllHabitsUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao
) {
    
    suspend fun execute() {
        habitDao.clear()
        dailyDao.clear()
    }
}