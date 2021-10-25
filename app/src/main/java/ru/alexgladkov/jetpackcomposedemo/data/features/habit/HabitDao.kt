package ru.alexgladkov.jetpackcomposedemo.data.features.habit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(entity: HabitEntity)

    @Query("SELECT * from ${HabitEntity.TABLE_HABIT_NAME}")
    suspend fun getAll(): List<HabitEntity>
}