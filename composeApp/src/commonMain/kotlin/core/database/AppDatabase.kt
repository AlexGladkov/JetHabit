package core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import core.database.dao.UserProfileDao
import core.database.entity.UserProfile
import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity

@Database(
    entities = [
        HabitEntity::class, 
        DailyEntity::class, 
        TrackerEntity::class,
        UserProfile::class
    ], 
    version = 7
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getHabitDao(): HabitDao
    abstract fun getDailyDao(): DailyDao
    abstract fun getTrackerDao(): TrackerDao
    abstract fun getUserProfileDao(): UserProfileDao
}

internal const val dbFileName = "jethabit.db"