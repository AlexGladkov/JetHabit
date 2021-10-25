package ru.alexgladkov.jetpackcomposedemo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyDao
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.habit.HabitDao
import ru.alexgladkov.jetpackcomposedemo.data.features.habit.HabitEntity

@Database(entities = [HabitEntity::class, DailyEntity::class], version = 2)
abstract class JetHabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun dailyDao(): DailyDao
}