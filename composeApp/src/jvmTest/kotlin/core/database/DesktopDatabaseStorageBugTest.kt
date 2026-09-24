package core.database

import feature.habits.data.HabitEntity
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopDatabaseStorageBugTest {
    @Test
    fun dataSurvivesDesktopTempDirectoryCleanup() = runBlocking {
        val previousTmp = System.getProperty("java.io.tmpdir")
        val previousHome = System.getProperty("user.home")
        val tempRoot = Files.createTempDirectory("jethabit-desktop-room-repro")
        val userHome = Files.createTempDirectory("jethabit-desktop-room-home")
        val habit = HabitEntity(
            id = "repro-habit",
            title = "Must survive cleanup",
            isGood = true,
            startDate = "2024-01-01",
            endDate = "2024-01-31",
            daysToCheck = "1111111",
        )
        try {
            System.setProperty("java.io.tmpdir", tempRoot.toString())
            System.setProperty("user.home", userHome.toString())
            val first = getRoomDatabase(getDatabaseBuilder())
            try {
                first.getHabitDao().insert(habit)
                assertEquals(listOf(habit), first.getHabitDao().getAll())
            } finally {
                first.close()
            }

            Files.walk(tempRoot).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach { path ->
                    if (path != tempRoot) Files.deleteIfExists(path)
                }
            }
            Files.createDirectories(tempRoot)

            val reopened = getRoomDatabase(getDatabaseBuilder())
            try {
                assertEquals(
                    listOf(habit),
                    reopened.getHabitDao().getAll(),
                    "Desktop Room data should survive cleanup of java.io.tmpdir",
                )
            } finally {
                reopened.close()
            }
        } finally {
            if (previousTmp == null) System.clearProperty("java.io.tmpdir")
            else System.setProperty("java.io.tmpdir", previousTmp)
            if (previousHome == null) System.clearProperty("user.home")
            else System.setProperty("user.home", previousHome)
            Files.walk(tempRoot).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach { path -> Files.deleteIfExists(path) }
            }
            Files.walk(userHome).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach { path -> Files.deleteIfExists(path) }
            }
        }
    }
}
