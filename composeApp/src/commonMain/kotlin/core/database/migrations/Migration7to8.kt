package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(connection: SQLiteConnection) {
        // Create ProjectEntity table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS ProjectEntity (
                id TEXT NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                colorHex TEXT NOT NULL
            )
        """.trimIndent())

        // Add projectId column to HabitEntity
        connection.execSQL("""
            ALTER TABLE HabitEntity ADD COLUMN projectId TEXT DEFAULT NULL
        """.trimIndent())
    }
}
