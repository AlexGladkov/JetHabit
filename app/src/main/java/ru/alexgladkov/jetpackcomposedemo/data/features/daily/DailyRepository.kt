package ru.alexgladkov.jetpackcomposedemo.data.features.daily

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.models.DailyHabitContainer
import javax.inject.Inject

data class DailyItem(
    val date: String,
    val habits: List<DailyHabitContainer>
)

class DailyRepository @Inject constructor(
    private val dailyDao: DailyDao
) {

    suspend fun fetchDiary(): List<DailyItem> = dailyDao.getAll()
        .map {
            DailyItem(
                date = it.date,
                habits = decompressHabitsWithValues(it.habitItemIdsWithStatuses)
            )
        }


    suspend fun addOrUpdate(date: String, habitId: Long, value: Boolean) {
        val records = fetchDiary()
        val recordForDate = records.firstOrNull { it.date == date }

        if (recordForDate == null) {
            dailyDao.insert(
                DailyEntity(
                    date = date,
                    habitItemIdsWithStatuses = compressHabitsWithValues(
                        listOf(DailyHabitContainer(habitId, value))
                    )
                )
            )
        } else {
            val dailyRecords = recordForDate.habits.toMutableList()
            val dailyRecord = dailyRecords.filter { it.habbitId == habitId }

            if (dailyRecord.isNotEmpty()) {
                dailyRecords.remove(dailyRecord.first())
            }

            dailyRecords.add(DailyHabitContainer(habitId, value))
            val compressed = compressHabitsWithValues(dailyRecords)
            dailyDao.update(entity = DailyEntity(date = date, habitItemIdsWithStatuses = compressed))
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