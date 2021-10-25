package ru.alexgladkov.jetpackcomposedemo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.alexgladkov.jetpackcomposedemo.data.database.JetHabitDatabase
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyDao
import ru.alexgladkov.jetpackcomposedemo.data.features.habit.HabitDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): JetHabitDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            JetHabitDatabase::class.java,
            "jet_habit_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideHabbitDao(jetHabitDatabase: JetHabitDatabase): HabitDao = jetHabitDatabase.habitDao()

    @Provides
    @Singleton
    fun provideDailyDao(jetHabitDatabase: JetHabitDatabase): DailyDao = jetHabitDatabase.dailyDao()
}