package core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity

@Database(entities = [HabitEntity::class, DailyEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getHabitDao(): HabitDao
    abstract fun getDailyDao(): DailyDao
}

internal const val dbFileName = "jethabit.db"