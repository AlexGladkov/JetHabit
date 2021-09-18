package ru.alexgladkov.jetpackcomposedemo.data.features.daily

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.models.DailyHabbitContainer
import javax.inject.Inject

data class DailyItem(
    val date: String,
    val habbits: List<DailyHabbitContainer>
)

class DailyRepository @Inject constructor(
    private val dailyDao: DailyDao
) {

    suspend fun fetchDiary(): List<DailyItem> = dailyDao.getAll()
        .map {
            DailyItem(
                date = it.date,
                habbits = decompressHabbitsWithValues(it.habbitItemIdsWithStatuses)
            )
        }


    suspend fun addOrUpdate(date: String, habbitId: Long, value: Boolean) {
        val records = fetchDiary()
        val recordForDate = records.firstOrNull { it.date == date }

        if (recordForDate == null) {
            dailyDao.insert(
                DailyEntity(
                    date = date,
                    habbitItemIdsWithStatuses = compressHabbitsWithValues(
                        listOf(DailyHabbitContainer(habbitId, value))
                    )
                )
            )
        } else {
            val dailyRecords = recordForDate.habbits.toMutableList()
            val dailyRecord = dailyRecords.filter { it.habbitId == habbitId }

            if (dailyRecord.isNotEmpty()) {
                dailyRecords.remove(dailyRecord.first())
            }

            dailyRecords.add(DailyHabbitContainer(habbitId, value))
            val compressed = compressHabbitsWithValues(dailyRecords)
            dailyDao.update(entity = DailyEntity(date = date, habbitItemIdsWithStatuses = compressed))
        }
    }

    @ExperimentalSerializationApi
    fun decompressHabbitsWithValues(input: String): List<DailyHabbitContainer> {
        return Json.decodeFromString(input)
    }

    @ExperimentalSerializationApi
    fun compressHabbitsWithValues(pairs: List<DailyHabbitContainer>): String {
        return Json.encodeToString(pairs)
    }
}