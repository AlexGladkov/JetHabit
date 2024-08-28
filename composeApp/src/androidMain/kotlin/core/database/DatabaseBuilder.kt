package core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val applicationContext = context.applicationContext
    val databaseFile = applicationContext.getDatabasePath("jethabit.db")
    return Room.databaseBuilder<AppDatabase>(
        context = applicationContext,
        name = databaseFile.absolutePath
    ).fallbackToDestructiveMigration(dropAllTables = true)
}