package feature.habits.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.habits.data.HabitType
import feature.habits.data.Measurement
import kotlinx.datetime.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class CreateHabitUseCase(
    private val habitDao: HabitDao
) {
    suspend fun execute(
        title: String,
        isGood: Boolean,
        type: HabitType,
        measurement: Measurement,
        startDate: String = "",
        endDate: String = "",
        projectId: String? = null
    ) {
        // Set default dates if not provided
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val defaultEndDate = today.plus(30, DateTimeUnit.DAY)

        val finalStartDate = if (startDate.isBlank()) today.toString() else startDate
        val finalEndDate = if (endDate.isBlank()) defaultEndDate.toString() else endDate

        val habitEntity = HabitEntity(
            id = UUID.generateUUID().toString(),
            title = title,
            isGood = isGood,
            startDate = finalStartDate,
            endDate = finalEndDate,
            daysToCheck = "[0,1,2,3,4,5,6]",
            type = type,
            measurement = measurement,
            projectId = projectId
        )

        habitDao.insert(habitEntity)
    }
}