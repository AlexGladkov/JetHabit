package core.database.migrations

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MigrationChain7to9Test {
    @Test
    fun multiHopMigrationFromVersion7ToVersion9PreservesData() {
        val connection = BundledSQLiteDriver().open(IN_MEMORY_DATABASE)
        try {
            createVersionSevenSchema(connection)
            seedVersionSevenData(connection)

            MIGRATION_7_8.migrate(connection)
            MIGRATION_8_9.migrate(connection)
            connection.execSQL("PRAGMA user_version = 9")

            // PRAGMA user_version is 9 after the multi-hop chain
            assertEquals(
                expected = VERSION_NINE,
                actual = connection.queryLong("PRAGMA user_version"),
                message = "PRAGMA user_version should be 9 after chain 7->8->9"
            )

            // v9 schema anchors present
            assertTrue(actual = SCHEDULE_TABLE in connection.tableNames())
            assertTrue(
                actual = "completedAtInstant" in connection.tableColumns(DAILY_TABLE)
            )
            // v8 intermediate artifact also present (chain fully applied)
            assertTrue(actual = PROJECT_TABLE in connection.tableNames())
            assertTrue(
                actual = "projectId" in connection.tableColumns(HABIT_TABLE)
            )

            // Seeded v7 rows preserved, no destructive path executed
            assertEquals(expected = 1L, actual = connection.queryLong("SELECT COUNT(*) FROM $HABIT_TABLE"))
            assertEquals(expected = 1L, actual = connection.queryLong("SELECT COUNT(*) FROM $DAILY_TABLE"))
            assertEquals(
                expected = "h1",
                actual = connection.queryText("SELECT id FROM $HABIT_TABLE")
            )
            assertEquals(
                expected = "Version seven habit",
                actual = connection.queryText("SELECT title FROM $HABIT_TABLE")
            )
            assertEquals(
                expected = "d1",
                actual = connection.queryText("SELECT id FROM $DAILY_TABLE")
            )
        } finally {
            connection.close()
        }
    }

    private fun createVersionSevenSchema(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $HABIT_TABLE (
                id TEXT NOT NULL,
                title TEXT NOT NULL,
                isGood INTEGER NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                daysToCheck TEXT NOT NULL,
                type TEXT NOT NULL,
                measurement TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $DAILY_TABLE (
                id TEXT NOT NULL,
                habitId TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                isChecked INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }

    private fun seedVersionSevenData(connection: SQLiteConnection) {
        connection.execSQL(
            """
            INSERT INTO $HABIT_TABLE (id, title, isGood, startDate, endDate, daysToCheck, type, measurement)
            VALUES ('h1', 'Version seven habit', 1, '2024-01-01', '2024-01-31', '1,2,3', 'REGULAR', 'KILOGRAMS')
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT INTO $DAILY_TABLE (id, habitId, timestamp, isChecked)
            VALUES ('d1', 'h1', '2024-01-02T08:00:00Z', 0)
            """.trimIndent()
        )
    }

    private fun SQLiteConnection.execSQL(sql: String) {
        val statement = prepare(sql)
        try {
            statement.step()
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.tableNames(): List<String> {
        val statement = prepare("SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name")
        try {
            val names = mutableListOf<String>()
            while (statement.step()) {
                names += statement.getText(FIRST_COLUMN_INDEX)
            }
            return names
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.tableColumns(tableName: String): List<String> {
        val statement = prepare("PRAGMA table_info($tableName)")
        try {
            val names = mutableListOf<String>()
            while (statement.step()) {
                names += statement.getText(COLUMN_NAME_INDEX)
            }
            return names
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.queryLong(sql: String): Long {
        val statement = prepare(sql)
        try {
            assertTrue(actual = statement.step())
            return statement.getLong(FIRST_COLUMN_INDEX)
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.queryText(sql: String): String {
        val statement = prepare(sql)
        try {
            assertTrue(actual = statement.step())
            return statement.getText(FIRST_COLUMN_INDEX)
        } finally {
            statement.close()
        }
    }

    private companion object {
        private const val VERSION_NINE = 9L
        private const val IN_MEMORY_DATABASE = ":memory:"
        private const val HABIT_TABLE = "HabitEntity"
        private const val DAILY_TABLE = "DailyEntity"
        private const val PROJECT_TABLE = "ProjectEntity"
        private const val SCHEDULE_TABLE = "ScheduleEntity"
        private const val FIRST_COLUMN_INDEX = 0
        private const val COLUMN_NAME_INDEX = 1
    }
}
