package feature.detail.domain

import feature.habits.data.HabitDao

class DeleteHabitUseCase(
    private val habitDao: HabitDao
) {
    
    suspend fun execute(habitId: String) {
        habitDao.deleteWith(habitId)
    }
}