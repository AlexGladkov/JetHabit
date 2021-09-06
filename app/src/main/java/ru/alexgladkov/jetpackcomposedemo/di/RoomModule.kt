package ru.alexgladkov.jetpackcomposedemo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.alexgladkov.jetpackcomposedemo.data.database.JetHabbitDatabase
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): JetHabbitDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            JetHabbitDatabase::class.java,
            "jet_habbit_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideHabbitDao(jetHabbitDatabase: JetHabbitDatabase): HabbitDao = jetHabbitDatabase.habbitDao()
}