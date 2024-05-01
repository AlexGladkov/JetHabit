package data.features.daily

import Database
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import data.features.daily.models.DailyHabitContainer

data class DailyItem(
    val date: String,
    val habits: List<DailyHabitContainer>
)

class DailyRepository(private val database: Database) {

    suspend fun fetchDiary(): List<DailyItem> {
        val result = database.dailyQueries.selectAll()
            .executeAsList()
            .filter {
                !it.date.contains("T")
            }
            .map {
                DailyItem(
                    date = it.date,
                    habits = decompressHabitsWithValues(it.habitItemIdsWithStatuses)
                )
            }

        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun addOrUpdate(date: String, habitId: Long, value: Boolean) {
        val records = fetchDiary()
        val recordForDate = records.firstOrNull { it.date == date }

        if (recordForDate == null) {
            database.dailyQueries
                .insert(date, compressHabitsWithValues(
                    listOf(DailyHabitContainer(habitId, value))
                ))
        } else {
            val dailyRecords = recordForDate.habits.toMutableList()
            val dailyRecord = dailyRecords.filter { it.habbitId == habitId }

            if (dailyRecord.isNotEmpty()) {
                dailyRecords.remove(dailyRecord.first())
            }

            dailyRecords.add(DailyHabitContainer(habitId, value))
            val compressed = compressHabitsWithValues(dailyRecords)
            database.dailyQueries.update(date, habitItemIdsWithStatuses = compressed)
        }
    }

    @ExperimentalSerializationApi
    fun decompressHabitsWithValues(input: String): List<DailyHabitContainer> {
        return Json.decodeFromString(input)
    }

    @ExperimentalSerializationApi
    fun compressHabitsWithValues(pairs: List<DailyHabitContainer>): String {
        return Json.encodeToString(pairs)
    }
}