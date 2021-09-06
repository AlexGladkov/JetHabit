package ru.alexgladkov.jetpackcomposedemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitDao
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideHabbitRepository(habbitDao: HabbitDao): HabbitRepository =
        HabbitRepository(habbitDao = habbitDao)
}