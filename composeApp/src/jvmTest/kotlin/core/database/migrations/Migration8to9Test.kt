package core.database.migrations

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class Migration8to9Test {
    @Test
    fun migration8to9CreatesRemindersAndAddsCompletedAt(): Unit = withVersionEightDatabase { connection ->
        insertVersionEightHabit(connection, "habit-1")
        insertVersionEightHabit(connection, "habit-2")
        insertVersionEightDaily(connection, "daily-1", "habit-1")
        insertVersionEightDaily(connection, "daily-2", "habit-2")

        MIGRATION_8_9.migrate(connection)
        connection.execSQL("PRAGMA user_version = 9")

        // HabitEntity rows preserved
        assertEquals(expected = 2L, actual = connection.queryLong("SELECT COUNT(*) FROM $HABIT_TABLE"))
        assertEquals(
            expected = "Version eight habit",
            actual = connection.queryText("SELECT title FROM $HABIT_TABLE WHERE id = 'habit-1'")
        )
        println("HabitEntity OK: 2 rows preserved after migration 8->9")

        // ReminderEntity table shape: columns exactly as expected, PK = habitId
        assertTrue(
            actual = REMINDER_TABLE in connection.tableNames(),
            message = "ReminderEntity table should be created by migration 8->9"
        )
        val reminderColumns = connection.tableColumns(REMINDER_TABLE)
        assertEquals(
            expected = listOf(
                "habitId", "hour", "minute", "daysMask", "timeZoneId", "adaptiveEnabled", "anchor"
            ),
            actual = reminderColumns.map { column -> column.name }
        )
        assertEquals(expected = listOf("habitId"), actual = connection.primaryKeyColumns(REMINDER_TABLE))
        assertTrue(actual = reminderColumns.first { it.name == "timeZoneId" }.notNull)
        assertTrue(actual = reminderColumns.first { it.name == "adaptiveEnabled" }.notNull)
        assertFalse(actual = reminderColumns.first { it.name == "anchor" }.notNull)

        // ReminderEntity habitId indexed
        val indexNames = connection.queryTexts(
            "SELECT name FROM sqlite_master WHERE type = 'index' AND tbl_name = '$REMINDER_TABLE'"
        )
        assertTrue(
            actual = indexNames.any { it.contains(REMINDER_TABLE) },
            message = "ReminderEntity should have an index on habitId"
        )

        // DailyEntity gained nullable completedAtEpochMs without DEFAULT; history stays NULL
        val dailyColumns = connection.tableColumns(DAILY_TABLE)
        val completedAt = dailyColumns.firstOrNull { it.name == COMPLETED_AT_COLUMN }
        assertNotNull(completedAt, "DailyEntity should gain completedAtEpochMs column")
        assertFalse(actual = completedAt.notNull, "completedAtEpochMs must be nullable")
        assertNull(actual = completedAt.defaultValue, "completedAtEpochMs must have no DEFAULT")
        assertEquals(
            expected = 2L,
            actual = connection.queryLong("SELECT COUNT(*) FROM $DAILY_TABLE WHERE $COMPLETED_AT_COLUMN IS NULL")
        )
        assertEquals(
            expected = 0L,
            actual = connection.queryLong(
                "SELECT COUNT(*) FROM $DAILY_TABLE WHERE $COMPLETED_AT_COLUMN IS NOT NULL AND $COMPLETED_AT_COLUMN != 1"
            )
        )
        println("DailyEntity.completedAtEpochMs OK: nullable column added, historical rows stay NULL")

        // user_version bumped
        assertEquals(expected = 9L, actual = connection.queryLong("PRAGMA user_version"))
    }

    @Test
    fun versionNineSchemaDocumentsRemindersShape(): Unit {
        val appDatabaseSource = readProjectFile(
            listOf(
                "composeApp/src/commonMain/kotlin/core/database/AppDatabase.kt",
                "src/commonMain/kotlin/core/database/AppDatabase.kt"
            )
        )
        assertEquals(expected = 1, actual = "version = $VERSION_NINE".toRegex().findAll(appDatabaseSource).count())
        assertTrue(actual = appDatabaseSource.contains("ReminderEntity::class"))
        assertTrue(actual = appDatabaseSource.contains("getReminderDao(): ReminderDao"))
        println("AppDatabase.kt OK: version = 9, ReminderEntity registered, ReminderDao exposed")

        val schemaDir = firstExistingDir(
            listOf(
                "composeApp/schemas/core.database.AppDatabase",
                "schemas/core.database.AppDatabase"
            )
        )
        val schema = File(schemaDir, "9.json").readText()
        val schema8 = File(schemaDir, "8.json").readText()
        assertTrue(actual = schema.contains("\"version\": $VERSION_NINE"))
        assertTrue(actual = schema.contains("\"tableName\": \"$REMINDER_TABLE\""))
        assertTrue(actual = schema.contains("\"tableName\": \"$DAILY_TABLE\""))
        assertTrue(actual = schema.contains(COMPLETED_AT_COLUMN))
        val hashOf = { text: String ->
            Regex("\"identityHash\": \"([^\"]+)\"").find(text)!!.groupValues[VALUE_GROUP_INDEX]
        }
        assertTrue(
            actual = hashOf(schema) != hashOf(schema8),
            message = "9.json identityHash must differ from 8.json"
        )
        println("schemas/9.json OK: version 9, ReminderEntity + DailyEntity.completedAtEpochMs, new identityHash")

        val builderPaths = listOf(
            "androidMain", "iosMain", "jvmMain"
        ).map { sourceSet ->
            firstExistingFile(
                listOf(
                    "composeApp/src/$sourceSet/kotlin/core/database/DatabaseBuilder.kt",
                    "src/$sourceSet/kotlin/core/database/DatabaseBuilder.kt"
                )
            )
        }
        builderPaths.forEach { path ->
            val source = File(path).readText()
            assertTrue(
                actual = "MIGRATION_8_9" in source || "ALL_MIGRATIONS" in source,
                message = "$path should register the 8->9 migration"
            )
            assertFalse(
                actual = source.contains("fallbackToDestructiveMigration"),
                message = "$path must not fall back to destructive migration"
            )
        }
        println("DatabaseBuilder.kt OK: android/ios/jvm all wired to migrations 8->9, no destructive fallback")
    }


    private fun withVersionEightDatabase(block: (SQLiteConnection) -> Unit) {
        val connection = BundledSQLiteDriver().open(IN_MEMORY_DATABASE)
        try {
            connection.execSQL(VERSION_EIGHT_HABIT_TABLE_SQL)
            connection.execSQL(VERSION_EIGHT_DAILY_TABLE_SQL)
            block(connection)
        } finally {
            connection.close()
        }
    }

    private fun insertVersionEightHabit(connection: SQLiteConnection, id: String) {
        connection.execSQL(
            """
            INSERT INTO $HABIT_TABLE (id, title, isGood, startDate, endDate, daysToCheck, type, measurement, projectId)
            VALUES ('$id', 'Version eight habit', 1, '2024-01-01', '2024-01-31', '1,2,3', 'REGULAR', 'KILOGRAMS', NULL)
            """.trimIndent()
        )
    }

    private fun insertVersionEightDaily(connection: SQLiteConnection, id: String, habitId: String) {
        connection.execSQL(
            """
            INSERT INTO $DAILY_TABLE (id, habitId, timestamp, isChecked)
            VALUES ('$id', '$habitId', '2024-01-01', 1)
            """.trimIndent()
        )
    }

    private fun readProjectFile(paths: List<String>): String = File(firstExistingFile(paths)).readText()

    private fun firstExistingFile(paths: List<String>): String {
        val file = paths.asSequence().map(::File).firstOrNull { candidate -> candidate.isFile }
        assertNotNull(file, "Expected one of the files to exist: ${paths.joinToString()}")
        return file.path
    }

    private fun firstExistingDir(paths: List<String>): File {
        val dir = paths.asSequence().map(::File).firstOrNull { candidate -> candidate.isDirectory }
        assertNotNull(dir, "Expected one of the dirs to exist: ${paths.joinToString()}")
        return dir
    }

    private fun SQLiteConnection.execSQL(sql: String) {
        val statement = prepare(sql)
        try {
            statement.step()
        } finally {
            statement.close()
        }
    }

    private fun SQLiteConnection.tableNames(): List<String> =
        queryTexts("SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name")

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
                if (statement.getLong(COLUMN_PRIMARY_KEY_INDEX) > 0L) {
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

    private fun androidx.sqlite.SQLiteStatement.getNullableText(index: Int): String? =
        if (isNull(index)) null else getText(index)

    private data class TableColumn(
        val name: String,
        val notNull: Boolean,
        val defaultValue: String?
    )

    private companion object {
        private const val VERSION_NINE = 9
        private const val IN_MEMORY_DATABASE = ":memory:"
        private const val HABIT_TABLE = "HabitEntity"
        private const val DAILY_TABLE = "DailyEntity"
        private const val REMINDER_TABLE = "ReminderEntity"
        private const val COMPLETED_AT_COLUMN = "completedAtEpochMs"
        private const val FK_TABLE_INDEX = 2
        private const val FK_ON_DELETE_INDEX = 6
        private const val FIRST_COLUMN_INDEX = 0
        private const val COLUMN_NAME_INDEX = 1
        private const val COLUMN_NOT_NULL_INDEX = 3
        private const val COLUMN_DEFAULT_VALUE_INDEX = 4
        private const val COLUMN_PRIMARY_KEY_INDEX = 5
        private const val VALUE_GROUP_INDEX = 1
        private const val TRUE_LONG = 1L
        private val VERSION_EIGHT_HABIT_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS $HABIT_TABLE (
                id TEXT NOT NULL,
                title TEXT NOT NULL,
                isGood INTEGER NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                daysToCheck TEXT NOT NULL,
                type TEXT NOT NULL,
                measurement TEXT NOT NULL,
                projectId TEXT DEFAULT NULL,
                PRIMARY KEY(id)
            )
        """.trimIndent()
        private val VERSION_EIGHT_DAILY_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS $DAILY_TABLE (
                id TEXT NOT NULL,
                habitId TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                isChecked INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
        """.trimIndent()
    }
}
