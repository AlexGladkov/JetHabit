package ru.alexgladkov.jetpackcomposedemo.data.features.habbit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabbitDao {

    @Insert
    suspend fun insert(entity: HabbitEntity)

    @Query("SELECT * from ${HabbitEntity.TABLE_HABBIT_NAME}")
    suspend fun getAll(): List<HabbitEntity>
}