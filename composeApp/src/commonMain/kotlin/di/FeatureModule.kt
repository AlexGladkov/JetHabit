package di

import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.detail.di.detailModule
import feature.habits.domain.CreateHabitUseCase
import feature.projects.di.projectModule
import feature.settings.domain.ClearAllHabitsUseCase
import feature.tracker.domain.UpdateTrackerValueUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun featureModule() = DI.Module("feature") {
    importAll(
        detailModule,
        projectModule
    )
    
    // Use Cases
    bind<GetHabitsForTodayUseCase>() with singleton { 
        GetHabitsForTodayUseCase(
            habitDao = instance(),
            trackerDao = instance(),
            dailyDao = instance()
        )
    }
    bind<SwitchHabitUseCase>() with singleton { 
        SwitchHabitUseCase(
            habitDao = instance(),
            dailyDao = instance()
        )
    }
    bind<CreateHabitUseCase>() with singleton { 
        CreateHabitUseCase(
            habitDao = instance()
        )
    }
    bind<UpdateTrackerValueUseCase>() with singleton { 
        UpdateTrackerValueUseCase(
            trackerDao = instance()
        )
    }
    bind<ClearAllHabitsUseCase>() with singleton {
        ClearAllHabitsUseCase(
            habitDao = instance(),
            dailyDao = instance()
        )
    }
}