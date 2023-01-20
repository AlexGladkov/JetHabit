package data.features.habit

import Database
import data.HabitEntity

class HabitRepository(private val database: Database) {

    suspend fun addNewHabit(title: String, isGood: Boolean) {
        database.habitQueries.insert(null, title, isGood)
    }

    suspend fun fetchHabitsList(): List<HabitEntity> =
        database.habitQueries.selectAll().executeAsList()
}