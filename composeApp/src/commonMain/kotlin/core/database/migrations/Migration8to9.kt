package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        // Add streak tracking columns to HabitEntity
        connection.execSQL("""
            ALTER TABLE HabitEntity ADD COLUMN currentStreak INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

        connection.execSQL("""
            ALTER TABLE HabitEntity ADD COLUMN longestStreak INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

        connection.execSQL("""
            ALTER TABLE HabitEntity ADD COLUMN lastCompletedDate TEXT DEFAULT NULL
        """.trimIndent())
    }
}
