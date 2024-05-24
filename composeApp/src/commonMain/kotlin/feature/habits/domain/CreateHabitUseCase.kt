package feature.habits.domain

import feature.habits.data.HabitDao

class CreateHabitUseCase(
    private val habitDao: HabitDao
) {
    
    suspend fun execute(title: String, isGood: Boolean) {
        
    }
}