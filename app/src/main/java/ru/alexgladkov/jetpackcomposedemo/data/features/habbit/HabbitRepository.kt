package ru.alexgladkov.jetpackcomposedemo.data.features.habbit

import javax.inject.Inject

class HabbitRepository @Inject constructor(
    private val habitDao: HabitDao
) {

    suspend fun addNewHabbit(habitEntity: HabitEntity) {
        habitDao.insert(habitEntity)
    }

    suspend fun fetchHabbitsList(): List<HabitEntity> =
        habitDao.getAll()
}