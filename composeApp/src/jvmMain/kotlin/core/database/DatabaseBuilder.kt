package core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import core.database.migrations.MIGRATION_7_8
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "jethabit.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    ).addMigrations(MIGRATION_7_8)
}