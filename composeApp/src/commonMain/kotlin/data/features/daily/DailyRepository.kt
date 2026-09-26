package data.features.daily

import di.Inject
import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import data.features.daily.models.DailyHabitContainer

data class DailyItem(
    val date: String,
    val habits: List<DailyHabitContainer>
)

class DailyRepository(
    private val dailyDao: DailyDao = Inject.instance()
) {

    suspend fun fetchDiary(): List<DailyItem> = dailyDao.getAll()
        .groupBy { it.timestamp }
        .map { (date, entries) ->
            DailyItem(
                date = date,
                habits = entries.map { DailyHabitContainer(it.habitId.toLong(), it.isChecked) }
            )
        }

    suspend fun addOrUpdate(date: String, habitId: Long, value: Boolean) {
        dailyDao.insert(
            DailyEntity(
                id = "$date:$habitId",
                habitId = habitId.toString(),
                timestamp = date,
                isChecked = value
            )
        )
    }

    @ExperimentalSerializationApi
    fun decompressHabitsWithValues(input: String): List<DailyHabitContainer> {
        require(input.isNotBlank()) { "Daily serialization must not be blank" }
        return try {
            Json.decodeFromString(input)
        } catch (error: SerializationException) {
            throw IllegalArgumentException("Malformed Daily serialization", error)
        }
    }

    @ExperimentalSerializationApi
    fun compressHabitsWithValues(pairs: List<DailyHabitContainer>): String {
        return Json.encodeToString(pairs)
    }
}