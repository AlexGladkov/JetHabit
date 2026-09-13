package core.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        // Deliberate pre-v7 policy: schema history before v7 is not maintained,
        // so databases on versions 1..6 are wiped instead of crashing.
        // All upgrades from v7 onward must go through explicit hand-written migrations.
        .fallbackToDestructiveMigrationFrom(true, 1, 2, 3, 4, 5, 6)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}