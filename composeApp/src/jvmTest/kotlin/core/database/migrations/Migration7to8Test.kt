package core.database.migrations

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class Migration7to8Test {
    @Test
    fun migrationCreatesProjectsAndKeepsExistingHabitData(): Unit = withVersionSevenDatabase { connection ->
        insertVersionSevenHabit(connection)

        MIGRATION_7_8.migrate(connection)

        val habitColumns = connection.tableColumns(HABIT_TABLE)
        val projectColumns = connection.tableColumns(PROJECT_TABLE)

        assertTrue(
            actual = PROJECT_TABLE in connection.tableNames(),
            message = "ProjectEntity table should be created by migration 7->8"
        )
        assertEquals(
            expected = listOf("id", "title", "colorHex"),
            actual = projectColumns.map { column -> column.name }
        )
        assertEquals(expected = listOf("id"), actual = connection.primaryKeyColumns(PROJECT_TABLE))

        val projectIdColumn = habitColumns.firstOrNull { column -> column.name == PROJECT_ID_COLUMN }
        assertNotNull(projectIdColumn)
        assertFalse(actual = projectIdColumn.notNull)
        assertEquals(expected = "NULL", actual = projectIdColumn.defaultValue)

        assertEquals(expected = 1L, actual = connection.queryLong("SELECT COUNT(*) FROM $HABIT_TABLE"))
        assertEquals(expected = "habit-1", actual = connection.queryText("SELECT id FROM $HABIT_TABLE"))
        assertEquals(expected = "Version seven habit", actual = connection.queryText("SELECT title FROM $HABIT_TABLE"))
        assertEquals(expected = "REGULAR", actual = connection.queryText("SELECT type FROM $HABIT_TABLE"))
        assertEquals(expected = "KILOGRAMS", actual = connection.queryText("SELECT measurement FROM $HABIT_TABLE"))
        assertNull(actual = connection.queryNullableText("SELECT $PROJECT_ID_COLUMN FROM $HABIT_TABLE"))
    }

    @Test
    fun versionEightSchemaDocumentsProjectsShape(): Unit {
        val databaseSource = readProjectFile(APP_DATABASE_SOURCE_PATHS)
        val schema = readProjectFile(VERSION_EIGHT_SCHEMA_PATHS)

        assertTrue(actual = databaseSource.contains("version = $VERSION_EIGHT"))
        assertTrue(actual = schema.contains("\"version\": $VERSION_EIGHT"))
        assertTrue(actual = schema.contains("\"tableName\": \"$PROJECT_TABLE\""))
        assertTrue(actual = schema.contains("`projectId` TEXT"))
        assertTrue(actual = schema.contains("`id` TEXT NOT NULL, `title` TEXT NOT NULL, `colorHex` TEXT NOT NULL"))
    }

    private fun withVersionSevenDatabase(block: (SQLiteConnection) -> Unit) {
        val connection = BundledSQLiteDriver().open(IN_MEMORY_DATABASE)
        try {
            connection.execSQL(VERSION_SEVEN_HABIT_TABLE_SQL)
            block(connection)
        } finally {
            connection.close()
        }
    }

    private fun insertVersionSevenHabit(connection: SQLiteConnection) {
        connection.execSQL(
            """
            INSERT INTO $HABIT_TABLE (id, title, isGood, startDate, endDate, daysToCheck, type, measurement)
            VALUES ('habit-1', 'Version seven habit', 1, '2024-01-01', '2024-01-31', '1,2,3', 'REGULAR', 'KILOGRAMS')
            """.trimIndent()
        )
    }

    private fun readProjectFile(paths: List<String>): String {
        val file = paths.asSequence()
            .map(::File)
            .firstOrNull { candidate -> candidate.isFile }

        assertNotNull(file, "Expected one of the project files to exist: ${paths.joinToString()}")
        return file.readText()
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
        private const val VERSION_EIGHT = 8
        private const val IN_MEMORY_DATABASE = ":memory:"
        private val APP_DATABASE_SOURCE_PATHS = listOf(
            "composeApp/src/commonMain/kotlin/core/database/AppDatabase.kt",
            "src/commonMain/kotlin/core/database/AppDatabase.kt"
        )
        private val VERSION_EIGHT_SCHEMA_PATHS = listOf(
            "composeApp/schemas/core.database.AppDatabase/8.json",
            "schemas/core.database.AppDatabase/8.json"
        )
        private const val HABIT_TABLE = "HabitEntity"
        private const val PROJECT_TABLE = "ProjectEntity"
        private const val PROJECT_ID_COLUMN = "projectId"
        private const val FIRST_COLUMN_INDEX = 0
        private const val COLUMN_NAME_INDEX = 1
        private const val COLUMN_NOT_NULL_INDEX = 3
        private const val COLUMN_DEFAULT_VALUE_INDEX = 4
        private const val COLUMN_PRIMARY_KEY_INDEX = 5
        private const val FALSE_LONG = 0L
        private const val TRUE_LONG = 1L
        private val VERSION_SEVEN_HABIT_TABLE_SQL = """
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
    }
}
