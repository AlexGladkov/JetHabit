package ru.alexgladkov.jetpackcomposedemo.data.features.habit

class HabitRepository constructor() {

    suspend fun addNewHabit(habitEntity: HabitEntity) {
//        habitDao.insert(habitEntity)
    }

    suspend fun fetchHabitsList(): List<HabitEntity> =
        emptyList() //habitDao.getAll()
}