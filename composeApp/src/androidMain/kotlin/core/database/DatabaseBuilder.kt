package core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import core.database.migrations.MIGRATION_7_8
import core.database.migrations.MIGRATION_8_9

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val applicationContext = context.applicationContext
    val databaseFile = applicationContext.getDatabasePath("jethabit.db")
    return Room.databaseBuilder<AppDatabase>(
        context = applicationContext,
        name = databaseFile.absolutePath
    )
        .addMigrations(MIGRATION_7_8, MIGRATION_8_9)
        .fallbackToDestructiveMigration(dropAllTables = true)
}