package ru.alexgladkov.jetpackcomposedemo.data.features.daily

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyDao {

    @Insert
    suspend fun insert(entity: DailyEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: DailyEntity)

    @Query("SELECT * from ${DailyEntity.TABLE_DAILY_NAME}")
    suspend fun getAll(): List<DailyEntity>
}