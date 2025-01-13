package core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity

@Database(entities = [HabitEntity::class, DailyEntity::class, TrackerEntity::class], version = 6)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getHabitDao(): HabitDao
    abstract fun getDailyDao(): DailyDao
    abstract fun getTrackerDao(): TrackerDao
}

internal const val dbFileName = "jethabit.db"