package core.database.migrations

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class Migration8to9Test {
    @Test
    fun migrationCreatesScheduleEntityAndCompletedAtInstantPreservingData() {
        val connection = BundledSQLiteDriver().open(IN_MEMORY_DATABASE)
        try {
            createVersionEightSchema(connection)
            seedVersionEightData(connection)

            MIGRATION_8_9.migrate(connection)
            connection.execSQL("PRAGMA user_version = 9")

            // user_version is 9 after migration
            assertEquals(
                expected = VERSION_NINE,
                actual = connection.queryLong("PRAGMA user_version"),
                message = "PRAGMA user_version should be 9 after migration 8->9"
            )

            // ScheduleEntity table exists with expected columns
            assertTrue(
                actual = SCHEDULE_TABLE in connection.tableNames(),
                message = "ScheduleEntity table should be created by migration 8->9"
            )
            val scheduleColumns = connection.tableColumns(SCHEDULE_TABLE)
            assertEquals(
                expected = listOf(
                    "id",
                    "habitId",
                    "timeOfDay",
                    "daysOfWeek",
                    "isEnabled",
                    "lastFiredAt"
                ),
                actual = scheduleColumns.map { column -> column.name }
            )
            assertEquals(expected = listOf("id"), actual = connection.primaryKeyColumns(SCHEDULE_TABLE))
            val isEnabledColumn = scheduleColumns.first { it.name == "isEnabled" }
            assertTrue(actual = isEnabledColumn.notNull)
            assertNull(actual = isEnabledColumn.defaultValue)
            val lastFiredAtColumn = scheduleColumns.first { it.name == "lastFiredAt" }
            assertFalse(actual = lastFiredAtColumn.notNull)
            assertNull(actual = lastFiredAtColumn.defaultValue)

            // DailyEntity has nullable completedAtInstant without a default
            val completedColumn = connection.tableColumns(DAILY_TABLE)
                .firstOrNull { column -> column.name == "completedAtInstant" }
            assertTrue(actual = completedColumn != null, "DailyEntity should gain completedAtInstant column")
            assertFalse(actual = completedColumn!!.notNull)
            assertNull(actual = completedColumn.defaultValue)

            // All pre-existing data is preserved
            assertEquals(expected = 2L, actual = connection.queryLong("SELECT COUNT(*) FROM $HABIT_TABLE"))
            assertEquals(expected = 3L, actual = connection.queryLong("SELECT COUNT(*) FROM $DAILY_TABLE"))

            assertEquals(
                expected = "habit-1",
                actual = connection.queryText("SELECT id FROM $HABIT_TABLE WHERE id = 'habit-1'")
            )
            assertEquals(
                expected = "First habit",
                actual = connection.queryText("SELECT title FROM $HABIT_TABLE WHERE id = 'habit-1'")
            )
            assertEquals(
                expected = "2024-01-05",
                actual = connection.queryText("SELECT timestamp FROM $DAILY_TABLE WHERE id = 'd2'")
            )
            assertEquals(
                expected = 1L,
                actual = connection.queryLong("SELECT isChecked FROM $DAILY_TABLE WHERE id = 'd3'")
            )
            // completedAtInstant is NULL on pre-existing rows
            assertNull(
                actual = connection.queryNullableText(
                    "SELECT completedAtInstant FROM $DAILY_TABLE WHERE id = 'd1'"
                )
            )
        } finally {
            connection.close()
        }
    }

    private fun createVersionEightSchema(connection: SQLiteConnection) {
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
                projectId TEXT,
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

    private fun seedVersionEightData(connection: SQLiteConnection) {
        connection.execSQL(
            """
            INSERT INTO $HABIT_TABLE (id, title, isGood, startDate, endDate, daysToCheck, type, measurement, projectId)
            VALUES
                ('habit-1', 'First habit', 1, '2024-01-01', '2024-01-31', '1,2,3', 'REGULAR', 'KILOGRAMS', NULL),
                ('habit-2', 'Second habit', 0, '2024-02-01', '2024-02-29', '4,5', 'TRACKER', 'KILOGRAMS', NULL)
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT INTO $DAILY_TABLE (id, habitId, timestamp, isChecked)
            VALUES
                ('d1', 'habit-1', '2024-01-02T08:00:00Z', 0),
                ('d2', 'habit-1', '2024-01-05', 1),
                ('d3', 'habit-2', '2024-02-03T21:30:00Z', 1)
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
        return queryTexts("SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name")
    }

    private fun SQLiteConnection.tableColumns(tableName: String): List<TableColumn> {
        val statement = prepare("PRAGMA table_info($tableName)")
        try {
            val columns = mutableListOf<TableColumn>()
            while (statement.step()) {
                columns += TableColumn(
                    name = statement.getText(COLUMN_NAME_INDEX),
                    notNull = statement.getLong(COLUMN_NOT_NULL_INDEX) == TRUE_LONG,
                    defaultValue = statement.getNullableText(COLUMN_DEFAULT_VALUE_INDEX)
                )
            }
            return columns
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.primaryKeyColumns(tableName: String): List<String> {
        val statement = prepare("PRAGMA table_info($tableName)")
        try {
            val columns = mutableListOf<String>()
            while (statement.step()) {
                if (statement.getLong(COLUMN_PRIMARY_KEY_INDEX) > FALSE_LONG) {
                    columns += statement.getText(COLUMN_NAME_INDEX)
                }
            }
            return columns
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

    private fun SQLiteConnection.queryNullableText(sql: String): String? {
        val statement = prepare(sql)
        try {
            assertTrue(actual = statement.step())
            return statement.getNullableText(FIRST_COLUMN_INDEX)
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.queryTexts(sql: String): List<String> {
        val statement = prepare(sql)
        try {
            val values = mutableListOf<String>()
            while (statement.step()) {
                values += statement.getText(FIRST_COLUMN_INDEX)
            }
            return values
        } finally {
            statement.close()
        }
    }

    private fun SQLiteStatement.getNullableText(index: Int): String? {
        return if (isNull(index)) null else getText(index)
    }

    private data class TableColumn(
        val name: String,
        val notNull: Boolean,
        val defaultValue: String?
    )

    private companion object {
        private const val VERSION_NINE = 9L
        private const val IN_MEMORY_DATABASE = ":memory:"
        private const val HABIT_TABLE = "HabitEntity"
        private const val DAILY_TABLE = "DailyEntity"
        private const val SCHEDULE_TABLE = "ScheduleEntity"
        private const val FIRST_COLUMN_INDEX = 0
        private const val COLUMN_NAME_INDEX = 1
        private const val COLUMN_NOT_NULL_INDEX = 3
        private const val COLUMN_DEFAULT_VALUE_INDEX = 4
        private const val COLUMN_PRIMARY_KEY_INDEX = 5
        private const val FALSE_LONG = 0L
        private const val TRUE_LONG = 1L
    }
}
