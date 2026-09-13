package core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import core.database.dao.ScheduleDao
import core.database.dao.UserProfileDao
import core.database.entity.ScheduleEntity
import core.database.entity.UserProfile
import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.projects.data.ProjectDao
import feature.projects.data.ProjectEntity
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity

@Database(
    entities = [
        HabitEntity::class,
        DailyEntity::class,
        TrackerEntity::class,
        UserProfile::class,
        ProjectEntity::class,
        ScheduleEntity::class
    ],
    version = 9
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getHabitDao(): HabitDao
    abstract fun getDailyDao(): DailyDao
    abstract fun getTrackerDao(): TrackerDao
    abstract fun getUserProfileDao(): UserProfileDao
    abstract fun getProjectDao(): ProjectDao
    abstract fun getScheduleDao(): ScheduleDao
}

internal const val dbFileName = "jethabit.db"