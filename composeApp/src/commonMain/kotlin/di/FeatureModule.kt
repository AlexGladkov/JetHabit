package di

import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.detail.di.detailModule
import feature.habits.domain.CreateHabitUseCase
import feature.habits.domain.HabitCheckedHook
import feature.habits.domain.HabitDeletedHook
import feature.projects.di.projectModule
import feature.reminders.domain.AdaptReminderScheduleUseCase
import feature.reminders.domain.AdaptiveReminderCheckedHook
import feature.reminders.domain.AdaptiveReminderDeletedHook
import feature.reminders.domain.ReminderMutationLock
import feature.reminders.domain.schedule.ScheduleEngine
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

    bind<ScheduleEngine>() with singleton { ScheduleEngine }
    bind<ReminderMutationLock>() with singleton { ReminderMutationLock() }
    bind<AdaptReminderScheduleUseCase>() with singleton {
        AdaptReminderScheduleUseCase(
            reminderDao = instance(),
            dailyDao = instance(),
            engine = instance(),
            scheduler = instance(),
            mutationLock = instance()
        )
    }
    bind<HabitCheckedHook>() with singleton {
        AdaptiveReminderCheckedHook(instance())
    }
    bind<HabitDeletedHook>() with singleton {
        AdaptiveReminderDeletedHook(instance(), instance(), instance())
    }

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
            dailyDao = instance(),
            checkedHook = instance()
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
