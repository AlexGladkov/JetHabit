package core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import core.database.migrations.ALL_MIGRATIONS

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val applicationContext = context.applicationContext
    val databaseFile = applicationContext.getDatabasePath("jethabit.db")
    return Room.databaseBuilder<AppDatabase>(
        context = applicationContext,
        name = databaseFile.absolutePath
    )
        .addMigrations(*ALL_MIGRATIONS)
}