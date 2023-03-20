package data.features.habit

import Database
import data.HabitEntity
import data.features.habit.models.Habit
import kotlinx.datetime.Instant

class HabitRepository(private val database: Database) {

    suspend fun addNewHabit(title: String, isGood: Boolean) {
        database.habitQueries.insert(null, title, if (isGood) 1 else 0, "",  "", -1)
    }

    suspend fun fetchHabitsList(): List<Habit> =
        database.habitQueries.selectAll().executeAsList()
            .map {
                val startDate = if (it.startDate.isNullOrBlank()) {
                    null
                } else {
                    Instant.fromEpochMilliseconds(it.startDate.toLong())
                }

                val endDate = if (it.endDate.isNullOrBlank()) {
                    null
                } else {
                    Instant.fromEpochMilliseconds(it.endDate.toLong())
                }

                Habit(itemId = it.itemID, title = it.title, startDate = startDate, endDate = endDate, isGood = it.isGood == 1L)
            }

    suspend fun updateDates(id: Long, startDate: Instant, endDate: Instant) {
        database.habitQueries.updateDate(
            startDate = startDate.toEpochMilliseconds().toString(),
            endDate = endDate.toEpochMilliseconds().toString(),
            itemID = id
        )
    }

    fun deleteItem(habitId: Long) {
        database.habitQueries.deleteItem(habitId)
    }
}