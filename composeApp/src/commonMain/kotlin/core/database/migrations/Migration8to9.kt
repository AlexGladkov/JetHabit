package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        // Create UserSettings table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS user_settings (
                id INTEGER NOT NULL PRIMARY KEY,
                isDarkMode INTEGER NOT NULL,
                style TEXT NOT NULL,
                textSize TEXT NOT NULL,
                paddingSize TEXT NOT NULL,
                cornerStyle TEXT NOT NULL
            )
        """.trimIndent())

        // Insert default settings
        connection.execSQL("""
            INSERT INTO user_settings (id, isDarkMode, style, textSize, paddingSize, cornerStyle)
            VALUES (1, 1, 'Orange', 'Medium', 'Medium', 'Rounded')
        """.trimIndent())
    }
}
