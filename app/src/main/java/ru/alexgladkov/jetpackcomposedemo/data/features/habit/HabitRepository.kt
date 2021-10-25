package ru.alexgladkov.jetpackcomposedemo.data.features.habit

import javax.inject.Inject

class HabitRepository @Inject constructor(
    private val habitDao: HabitDao
) {

    suspend fun addNewHabit(habitEntity: HabitEntity) {
        habitDao.insert(habitEntity)
    }

    suspend fun fetchHabitsList(): List<HabitEntity> =
        habitDao.getAll()
}