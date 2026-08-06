package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        // Create activity_feed table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS activity_feed (
                id TEXT NOT NULL PRIMARY KEY,
                habitId TEXT NOT NULL,
                habitTitle TEXT NOT NULL,
                type TEXT NOT NULL,
                streakCount INTEGER NOT NULL,
                date TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """.trimIndent())

        // Add index on timestamp for better query performance
        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_activity_feed_timestamp ON activity_feed(timestamp)
        """.trimIndent())

        // Add index on habitId for better query performance
        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_activity_feed_habitId ON activity_feed(habitId)
        """.trimIndent())
    }
}
