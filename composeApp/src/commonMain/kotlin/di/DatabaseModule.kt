package di

import core.database.AppDatabase
import core.database.dao.UserProfileDao
import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.projects.data.ProjectDao
import feature.tracker.data.TrackerDao
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        get(qualifier = org.koin.core.qualifier.named("appDatabase"))
    }

    single<HabitDao> {
        get<AppDatabase>().getHabitDao()
    }

    single<TrackerDao> {
        get<AppDatabase>().getTrackerDao()
    }

    single<DailyDao> {
        get<AppDatabase>().getDailyDao()
    }

    single<UserProfileDao> {
        get<AppDatabase>().getUserProfileDao()
    }

    single<ProjectDao> {
        get<AppDatabase>().getProjectDao()
    }
} 