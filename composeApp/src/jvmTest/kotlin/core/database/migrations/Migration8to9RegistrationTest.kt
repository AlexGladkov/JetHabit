package core.database.migrations

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.io.File

internal class Migration8to9RegistrationTest {
    @Test
    fun allPlatformBuildersRegisterMigration8to9() {
        assertTrue(
            actual = androidBuilder().contains("addMigrations(MIGRATION_7_8, MIGRATION_8_9)"),
            message = "androidMain DatabaseBuilder.kt must register MIGRATION_8_9"
        )
        assertTrue(
            actual = iosBuilder().contains("addMigrations(MIGRATION_7_8, MIGRATION_8_9)"),
            message = "iosMain DatabaseBuilder.kt must register MIGRATION_8_9"
        )
        assertTrue(
            actual = jvmBuilder().contains("addMigrations(MIGRATION_7_8, MIGRATION_8_9)"),
            message = "jvmMain DatabaseBuilder.kt must register MIGRATION_8_9"
        )
    }

    @Test
    fun noUnqualifiedDestructiveFallbackInCommonDatabaseOrAndroidBuilder() {
        for (source in listOf(commonDatabase(), androidBuilder())) {
            assertFalse(
                actual = source.contains(UNQUALIFIED_DESTRUCTIVE_FALLBACK),
                message = "Unqualified fallbackToDestructiveMigration( must be removed"
            )
            assertTrue(
                actual = source.contains("fallbackToDestructiveMigrationFrom(true, 1, 2, 3, 4, 5, 6)"),
                message = "Targeted pre-v7 destructive fallback must be present"
            )
        }
    }

    @Test
    fun appDatabaseDeclaresVersion9ScheduleEntityAndDao() {
        val source = appDatabase()
        assertTrue(actual = source.contains("version = 9"))
        assertTrue(actual = source.contains("ScheduleEntity::class"))
        assertTrue(actual = source.contains("getScheduleDao(): ScheduleDao"))
    }

    @Test
    fun schemaNineIsCommittedAndConsistent() {
        val schema = schemaNine()
        assertTrue(actual = schema.contains("\"version\": 9"))
        assertTrue(actual = schema.contains("\"tableName\": \"ScheduleEntity\""))
        assertTrue(actual = schema.contains("completedAtInstant"))
        assertTrue(
            actual = schema.contains("`completedAtInstant` INTEGER"),
            message = "DailyEntity createSql in 9.json must contain completedAtInstant INTEGER"
        )
    }

    private fun read(paths: List<String>): String {
        val file = paths.asSequence().map(::File).firstOrNull { it.isFile }
        assertNotNull(file, "Expected one of the files to exist: ${paths.joinToString()}")
        return file.readText()
    }

    private fun androidBuilder() = read(listOf(
        "composeApp/src/androidMain/kotlin/core/database/DatabaseBuilder.kt",
        "src/androidMain/kotlin/core/database/DatabaseBuilder.kt"
    ))

    private fun iosBuilder() = read(listOf(
        "composeApp/src/iosMain/kotlin/core/database/DatabaseBuilder.kt",
        "src/iosMain/kotlin/core/database/DatabaseBuilder.kt"
    ))

    private fun jvmBuilder() = read(listOf(
        "composeApp/src/jvmMain/kotlin/core/database/DatabaseBuilder.kt",
        "src/jvmMain/kotlin/core/database/DatabaseBuilder.kt"
    ))

    private fun commonDatabase() = read(listOf(
        "composeApp/src/commonMain/kotlin/core/database/Database.kt",
        "src/commonMain/kotlin/core/database/Database.kt"
    ))

    private fun appDatabase() = read(listOf(
        "composeApp/src/commonMain/kotlin/core/database/AppDatabase.kt",
        "src/commonMain/kotlin/core/database/AppDatabase.kt"
    ))

    private fun schemaNine() = read(listOf(
        "composeApp/schemas/core.database.AppDatabase/9.json",
        "schemas/core.database.AppDatabase/9.json"
    ))

    private companion object {
        private const val UNQUALIFIED_DESTRUCTIVE_FALLBACK = "fallbackToDestructiveMigration("
    }
}
