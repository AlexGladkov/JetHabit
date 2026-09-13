package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        // Create ReminderEntity table with habitId as the one-to-one primary key.
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS ReminderEntity (
                habitId TEXT NOT NULL,
                hour INTEGER NOT NULL,
                minute INTEGER NOT NULL,
                daysMask INTEGER NOT NULL,
                timeZoneId TEXT NOT NULL,
                adaptiveEnabled INTEGER NOT NULL DEFAULT 0,
                anchor INTEGER,
                PRIMARY KEY(habitId)
            )
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_ReminderEntity_habitId ON ReminderEntity(habitId)
        """.trimIndent())

        // Historical daily rows keep completedAtEpochMs = NULL
        connection.execSQL("""
            ALTER TABLE DailyEntity ADD COLUMN completedAtEpochMs INTEGER
        """.trimIndent())
    }
}
