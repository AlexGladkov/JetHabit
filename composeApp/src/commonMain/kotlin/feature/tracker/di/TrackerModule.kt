package feature.tracker.di

import core.database.AppDatabase
import feature.tracker.data.TrackerDao
import feature.tracker.domain.UpdateTrackerValueUseCase
import org.koin.dsl.module

val trackerModule = module {
    single<TrackerDao> {
        val appDatabase = get<AppDatabase>()
        appDatabase.getTrackerDao()
    }

    factory<UpdateTrackerValueUseCase> {
        UpdateTrackerValueUseCase(get())
    }
} 