package feature.habits.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class CreateHabitUseCase(
    private val json: Json,
    private val habitDao: HabitDao
) {

    suspend fun execute(title: String, isGood: Boolean, days: JsonArray) {
        val entity = HabitEntity(
            id = UUID.generateUUID().toString(),
            title = title,
            isGood = isGood,
            daysToCheck = json.encodeToString(days),
            startDate = "",
            endDate = ""
        )

        habitDao.insert(entity)
    }
}