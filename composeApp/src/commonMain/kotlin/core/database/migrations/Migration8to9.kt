package core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        // Add vkId column to user_profile table
        connection.execSQL("""
            ALTER TABLE user_profile ADD COLUMN vkId TEXT DEFAULT NULL
        """.trimIndent())

        // Add authProvider column to user_profile table
        connection.execSQL("""
            ALTER TABLE user_profile ADD COLUMN authProvider TEXT DEFAULT NULL
        """.trimIndent())
    }
}
