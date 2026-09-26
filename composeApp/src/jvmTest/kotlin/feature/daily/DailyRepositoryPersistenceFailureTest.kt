package feature.daily

import androidx.room.Room
import core.database.AppDatabase
import core.database.getRoomDatabase
import data.features.daily.DailyRepository
import java.nio.file.Files
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DailyRepositoryPersistenceFailureTest {
    @Test
    fun addOrUpdateThenFetchDiarySurvivesRoomCloseAndReopen() = runBlocking {
        val databaseFile = Files.createTempFile("jethabit-daily-persistence", ".db")
        Files.deleteIfExists(databaseFile)
        try {
            val first = openDatabase(databaseFile.toString())
            try {
                val repository = DailyRepository(first.getDailyDao())
                repository.addOrUpdate("2025-01-15", 42L, false)
                repository.addOrUpdate("2025-01-15", 42L, true)
                assertEquals(true, repository.fetchDiary().single().habits.single().value)
            } finally {
                first.close()
            }

            val reopened = openDatabase(databaseFile.toString())
            try {
                val persisted = DailyRepository(reopened.getDailyDao()).fetchDiary()
                assertEquals("2025-01-15", persisted.single().date)
                assertEquals(42L, persisted.single().habits.single().habbitId)
                assertEquals(true, persisted.single().habits.single().value)
            } finally {
                reopened.close()
            }
        } finally {
            Files.deleteIfExists(databaseFile)
            Files.deleteIfExists(databaseFile.resolveSibling(databaseFile.fileName.toString() + "-wal"))
            Files.deleteIfExists(databaseFile.resolveSibling(databaseFile.fileName.toString() + "-shm"))
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun malformedDailySerializationFailsExplicitly() {
        runBlocking {
        val databaseFile = Files.createTempFile("jethabit-daily-malformed", ".db")
        Files.deleteIfExists(databaseFile)
        try {
            val database = openDatabase(databaseFile.toString())
            try {
                val repository = DailyRepository(database.getDailyDao())
                assertFailsWith<IllegalArgumentException> {
                    repository.decompressHabitsWithValues("not-json")
                }
            } finally {
                database.close()
            }
        } finally {
            Files.deleteIfExists(databaseFile)
            Files.deleteIfExists(databaseFile.resolveSibling(databaseFile.fileName.toString() + "-wal"))
            Files.deleteIfExists(databaseFile.resolveSibling(databaseFile.fileName.toString() + "-shm"))
        }
        }
    }

    private fun openDatabase(path: String): AppDatabase = getRoomDatabase(
        Room.databaseBuilder<AppDatabase>(name = path),
    )
}
