package feature.daily.domain

import feature.daily.data.DailyDao
import feature.habits.data.HabitDao

class SwitchHabitUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao
) {
    
    suspend fun execute(habitId: Long) {
        
    }
}