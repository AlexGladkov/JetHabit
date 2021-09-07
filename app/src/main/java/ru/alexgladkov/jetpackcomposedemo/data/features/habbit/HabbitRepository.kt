package ru.alexgladkov.jetpackcomposedemo.data.features.habbit

import javax.inject.Inject

class HabbitRepository @Inject constructor(
    private val habbitDao: HabbitDao
) {

    suspend fun addNewHabbit(habbitEntity: HabbitEntity) {
        habbitDao.insert(habbitEntity)
    }
}