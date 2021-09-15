package ru.alexgladkov.jetpackcomposedemo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyDao
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitDao
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitEntity

@Database(entities = [HabbitEntity::class, DailyEntity::class], version = 2)
abstract class JetHabbitDatabase : RoomDatabase() {
    abstract fun habbitDao(): HabbitDao
    abstract fun dailyDao(): DailyDao
}