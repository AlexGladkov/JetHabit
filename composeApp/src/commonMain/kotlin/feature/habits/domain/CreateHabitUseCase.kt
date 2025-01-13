package feature.habits.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.habits.data.HabitType
import feature.habits.data.Measurement
import java.util.UUID

class CreateHabitUseCase(
    private val habitDao: HabitDao
) {
    suspend fun execute(
        title: String,
        isGood: Boolean,
        type: HabitType,
        measurement: Measurement
    ) {
        val habitEntity = HabitEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            isGood = isGood,
            startDate = "",
            endDate = "",
            daysToCheck = "[0,1,2,3,4,5,6]",
            type = type,
            measurement = measurement
        )

        habitDao.insert(habitEntity)
    }
}