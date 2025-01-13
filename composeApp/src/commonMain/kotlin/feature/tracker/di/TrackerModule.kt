package feature.tracker.di

import core.database.AppDatabase
import feature.tracker.data.TrackerDao
import feature.tracker.domain.UpdateTrackerValueUseCase
import org.kodein.di.*

val trackerModule = DI.Module("TrackerModule") {
    bind<TrackerDao>() with singleton {
        val appDatabase = instance<AppDatabase>()
        appDatabase.getTrackerDao()
    }
    
    bind<UpdateTrackerValueUseCase>() with provider {
        UpdateTrackerValueUseCase(instance())
    }
} 