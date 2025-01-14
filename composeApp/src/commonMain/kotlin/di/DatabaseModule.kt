package di

import core.database.AppDatabase
import core.database.dao.UserProfileDao
import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.tracker.data.TrackerDao
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun databaseModule() = DI.Module("database") {
    bind<AppDatabase>() with singleton { 
        instance<Any>("appDatabase") as AppDatabase 
    }
    
    bind<HabitDao>() with singleton { 
        instance<AppDatabase>().getHabitDao()
    }
    
    bind<TrackerDao>() with singleton { 
        instance<AppDatabase>().getTrackerDao()
    }

    bind<DailyDao>() with singleton {
        instance<AppDatabase>().getDailyDao()
    }

    bind<UserProfileDao>() with singleton {
        instance<AppDatabase>().getUserProfileDao()
    }
} 