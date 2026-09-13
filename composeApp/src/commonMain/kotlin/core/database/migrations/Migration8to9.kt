package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        // Create ScheduleEntity table (matches schemas/core.database.AppDatabase/9.json)
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS ScheduleEntity (
                id TEXT NOT NULL,
                habitId TEXT NOT NULL,
                timeOfDay TEXT NOT NULL,
                daysOfWeek TEXT NOT NULL,
                isEnabled INTEGER NOT NULL,
                lastFiredAt INTEGER,
                PRIMARY KEY(id)
            )
        """.trimIndent())

        // Add completedAtInstant column to DailyEntity (nullable, no default)
        connection.execSQL("""
            ALTER TABLE DailyEntity ADD COLUMN completedAtInstant INTEGER
        """.trimIndent())
    }
}
