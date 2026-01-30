package di

import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.detail.di.detailModule
import feature.habits.domain.CreateHabitUseCase
import feature.projects.di.projectModule
import feature.settings.domain.ClearAllHabitsUseCase
import feature.tracker.domain.UpdateTrackerValueUseCase
import org.koin.dsl.module

val featureModule = module {
    includes(
        detailModule,
        projectModule
    )

    // Use Cases
    single<GetHabitsForTodayUseCase> {
        GetHabitsForTodayUseCase(
            habitDao = get(),
            trackerDao = get(),
            dailyDao = get()
        )
    }
    single<SwitchHabitUseCase> {
        SwitchHabitUseCase(
            habitDao = get(),
            dailyDao = get()
        )
    }
    single<CreateHabitUseCase> {
        CreateHabitUseCase(
            habitDao = get()
        )
    }
    single<UpdateTrackerValueUseCase> {
        UpdateTrackerValueUseCase(
            trackerDao = get()
        )
    }
    single<ClearAllHabitsUseCase> {
        ClearAllHabitsUseCase(
            habitDao = get(),
            dailyDao = get()
        )
    }
}