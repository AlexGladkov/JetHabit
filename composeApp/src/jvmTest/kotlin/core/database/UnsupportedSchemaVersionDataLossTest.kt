package core.database

import androidx.room.Room
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import core.database.entity.UserProfile
import core.database.migrations.MIGRATION_7_8
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UnsupportedSchemaVersionDataLossTest {
    @Test
    fun unsupportedUpgradeDoesNotEraseExistingProfile(): Unit = runBlocking {
        assertUnsupportedVersionDoesNotEraseExistingProfile(UNSUPPORTED_OLD_VERSION)
    }

    @Test
    fun unsupportedDowngradeDoesNotEraseExistingProfile(): Unit = runBlocking {
        assertUnsupportedVersionDoesNotEraseExistingProfile(UNSUPPORTED_NEW_VERSION)
    }

    private suspend fun assertUnsupportedVersionDoesNotEraseExistingProfile(unsupportedVersion: Int) {
        val databaseFile = File.createTempFile("jethabit-unsupported-version-", ".db")
        var seededDatabase: AppDatabase? = null
        try {
            seededDatabase = openDatabase(databaseFile)
            seededDatabase.getUserProfileDao().insertOrUpdateProfile(
                UserProfile(
                    name = EXPECTED_NAME,
                    email = "profile@example.com",
                    phoneNumber = "+1-555-0100",
                    avatarUri = null
                )
            )
            assertEquals(
                expected = EXPECTED_NAME,
                actual = seededDatabase.getUserProfileDao().getUserProfile().first()?.name
            )
            seededDatabase.close()
            seededDatabase = null

            setUserVersion(databaseFile, unsupportedVersion)

            var reopenedDatabase: AppDatabase? = null
            try {
                reopenedDatabase = openDatabase(databaseFile)
                assertEquals(
                    expected = EXPECTED_NAME,
                    actual = reopenedDatabase.getUserProfileDao().getUserProfile().first()?.name,
                    message = "Unsupported schema migration must not silently erase user data"
                )
            } catch (_: Throwable) {
                // A safe implementation may reject an unsupported schema. In that case,
                // the backing file must still contain the seeded row.
                assertEquals(expected = EXPECTED_NAME, actual = readProfileName(databaseFile))
            } finally {
                reopenedDatabase?.close()
            }
        } finally {
            seededDatabase?.close()
            databaseFile.delete()
            File(databaseFile.path + "-wal").delete()
            File(databaseFile.path + "-shm").delete()
            File(databaseFile.path + "-journal").delete()
        }
    }

    private fun openDatabase(databaseFile: File): AppDatabase {
        return getRoomDatabase(
            Room.databaseBuilder<AppDatabase>(name = databaseFile.absolutePath)
                .addMigrations(MIGRATION_7_8)
        )
    }

    private fun setUserVersion(databaseFile: File, version: Int) {
        val connection = BundledSQLiteDriver().open(databaseFile.absolutePath)
        try {
            connection.execSQL("PRAGMA user_version = $version")
        } finally {
            connection.close()
        }
    }

    private fun readProfileName(databaseFile: File): String? {
        val connection = BundledSQLiteDriver().open(databaseFile.absolutePath)
        return try {
            val statement = connection.prepare("SELECT name FROM user_profile WHERE id = 1")
            try {
                if (statement.step()) statement.getNullableText(0) else null
            } finally {
                statement.close()
            }
        } catch (_: Throwable) {
            null
        } finally {
            connection.close()
        }
    }

    private fun SQLiteConnection.execSQL(sql: String) {
        val statement = prepare(sql)
        try {
            statement.step()
        } finally {
            statement.close()
        }
    }

    private fun androidx.sqlite.SQLiteStatement.getNullableText(index: Int): String? {
        return if (isNull(index)) null else getText(index)
    }

    private companion object {
        private const val EXPECTED_NAME = "Data must survive"
        private const val UNSUPPORTED_OLD_VERSION = 6
        private const val UNSUPPORTED_NEW_VERSION = 9
    }
}
