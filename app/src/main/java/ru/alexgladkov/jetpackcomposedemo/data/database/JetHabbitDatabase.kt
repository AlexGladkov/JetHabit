package ru.alexgladkov.jetpackcomposedemo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitDao
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitEntity

@Database(entities = [HabbitEntity::class], version = 1)
abstract class JetHabbitDatabase : RoomDatabase() {
    abstract fun habbitDao(): HabbitDao
}