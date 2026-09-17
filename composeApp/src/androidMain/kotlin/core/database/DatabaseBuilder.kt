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
        // Registers Migration8to9 (MIGRATION_8_9) for the schedule feature.
        .addMigrations(MIGRATION_7_8, MIGRATION_8_9)
        // Deliberate pre-v7 policy: versions 1..6 have no maintained schema
        // history and are wiped; v7+ upgrades use explicit hand-written migrations.
        .fallbackToDestructiveMigrationFrom(true, 1, 2, 3, 4, 5, 6)
}