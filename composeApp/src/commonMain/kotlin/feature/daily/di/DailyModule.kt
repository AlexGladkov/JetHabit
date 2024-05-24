package feature.daily.di

import core.database.AppDatabase
import di.Inject.instance
import feature.daily.data.DailyDao
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import org.kodein.di.singleton

val dailyModule = DI.Module("DailyModule") {
    bind<DailyDao>() with singleton {
        val appDatabase = instance<AppDatabase>()
        appDatabase.getDailyDao()
    }
    
    bind<GetHabitsForTodayUseCase>() with provider {
        GetHabitsForTodayUseCase(instance(), instance(), instance())
    }
    
    bind<SwitchHabitUseCase>() with provider {
        SwitchHabitUseCase(instance(), instance())
    }
}