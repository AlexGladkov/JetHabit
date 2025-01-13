package feature.habits.di

import core.database.AppDatabase
import feature.habits.data.HabitDao
import feature.habits.domain.CreateHabitUseCase
import org.kodein.di.*

val habitModule = DI.Module("HabitModule") {
    bind<HabitDao>() with singleton {
        val appDatabase = instance<AppDatabase>()
        appDatabase.getHabitDao()
    }
    
    bind<CreateHabitUseCase>() with provider {
        CreateHabitUseCase(instance())
    }
}