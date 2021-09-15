package ru.alexgladkov.jetpackcomposedemo.data.features.daily

import javax.inject.Inject

class DailyRepository @Inject constructor(
    val dailyDao: DailyDao
) {

    suspend fun fetchDiary(): List<DailyEntity> = dailyDao.getAll()

    suspend fun addOrUpdate(date: String, habbitId: Int, value: Boolean) {
        val records = fetchDiary()
        val recordForDate = records.firstOrNull { it.date == date }

        if (recordForDate == null) {
            dailyDao.insert(
                DailyEntity(
                    date = date,
                    habbitItemIdsWithStatuses = compressHabbitsWithValues(
                        listOf(Pair(habbitId, value))
                    )
                )
            )
        } else {
            val values = decompressHabbitsWithValues(recordForDate.habbitItemIdsWithStatuses)
        }
    }

    fun decompressHabbitsWithValues(input: String): List<Pair<Int, Boolean>> {

    }

    fun compressHabbitsWithValues(pairs: List<Pair<Int, Boolean>>): String {

    }
}