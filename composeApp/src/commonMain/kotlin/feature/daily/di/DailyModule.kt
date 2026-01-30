package feature.daily.di

import core.database.AppDatabase
import data.features.daily.DailyRepository
import feature.daily.data.DailyDao
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import org.koin.dsl.module

val dailyModule = module {
    single<DailyDao> {
        val appDatabase = get<AppDatabase>()
        appDatabase.getDailyDao()
    }

    factory<GetHabitsForTodayUseCase> {
        GetHabitsForTodayUseCase(get(), get(), get())
    }

    factory<SwitchHabitUseCase> {
        SwitchHabitUseCase(get(), get())
    }

    factory<DailyRepository> {
        DailyRepository()
    }
}