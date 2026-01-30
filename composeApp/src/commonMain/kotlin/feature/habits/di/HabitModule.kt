package feature.habits.di

import core.database.AppDatabase
import feature.habits.data.HabitDao
import feature.habits.domain.CreateHabitUseCase
import org.koin.dsl.module

val habitModule = module {
    single<HabitDao> {
        val appDatabase = get<AppDatabase>()
        appDatabase.getHabitDao()
    }

    factory<CreateHabitUseCase> {
        CreateHabitUseCase(get())
    }
}