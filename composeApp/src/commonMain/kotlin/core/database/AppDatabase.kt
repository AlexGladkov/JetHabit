package core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity

@Database(entities = [HabitEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getHabitDao(): HabitDao
}