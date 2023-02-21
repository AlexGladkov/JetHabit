package data.features.habit

import Database
import data.HabitEntity

class HabitRepository(private val database: Database) {

    suspend fun addNewHabit(title: String, isGood: Boolean) {
        database.habitQueries.insert(null, title, if (isGood) 1 else 0)
    }

    suspend fun fetchHabitsList(): List<HabitEntity> =
        database.habitQueries.selectAll().executeAsList()

    fun deleteItem(habitId: Long) {
        database.habitQueries.deleteItem(habitId)
    }
}