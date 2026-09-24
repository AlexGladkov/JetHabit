package core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import core.database.migrations.MIGRATION_7_8
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Locale

private const val applicationDirectoryName = "JetHabit"
private val databaseSidecarSuffixes = listOf("-wal", "-shm", "-journal")

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val applicationDataDirectory = getApplicationDataDirectory()
    val databaseFile = File(applicationDataDirectory, dbFileName)
    System.getProperty("java.io.tmpdir")
        ?.takeIf(String::isNotBlank)
        ?.let { preserveLegacyDatabase(File(it, dbFileName), databaseFile) }

    return Room.databaseBuilder<AppDatabase>(
        name = databaseFile.absolutePath,
    ).addMigrations(MIGRATION_7_8)
}

private fun getApplicationDataDirectory(): File {
    val userHome = System.getProperty("user.home")
        ?.takeIf(String::isNotBlank)
        ?: throw IllegalStateException("Unable to locate the current user's home directory")
    val osName = System.getProperty("os.name", "").lowercase(Locale.ROOT)
    val baseDirectory = when {
        osName.contains("win") -> System.getenv("APPDATA")
            ?.takeIf(String::isNotBlank)
            ?: System.getenv("LOCALAPPDATA")?.takeIf(String::isNotBlank)
            ?: File(userHome, "AppData/Roaming").path
        osName.contains("mac") || osName.contains("darwin") ->
            File(userHome, "Library/Application Support").path
        else -> System.getenv("XDG_DATA_HOME")
            ?.takeIf(String::isNotBlank)
            ?: File(userHome, ".local/share").path
    }
    val directory = File(baseDirectory, applicationDirectoryName)

    try {
        Files.createDirectories(directory.toPath())
    } catch (exception: IOException) {
        throw IllegalStateException(
            "Unable to create the JetHabit application-data directory: ${directory.absolutePath}",
            exception,
        )
    }
    if (!directory.isDirectory || !Files.isWritable(directory.toPath())) {
        throw IllegalStateException(
            "JetHabit application-data directory is not a writable directory: ${directory.absolutePath}",
        )
    }
    return directory
}

private fun preserveLegacyDatabase(legacyDatabase: File, databaseFile: File) {
    if (databaseFile.exists() || !legacyDatabase.isFile) return

    val filesToCopy = listOf(legacyDatabase) + databaseSidecarSuffixes.map { suffix ->
        File(legacyDatabase.path + suffix)
    }.filter(File::isFile)

    try {
        filesToCopy.forEach { source ->
            val target = if (source == legacyDatabase) {
                databaseFile
            } else {
                File(databaseFile.path + source.name.removePrefix(legacyDatabase.name))
            }
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.COPY_ATTRIBUTES)
        }
    } catch (exception: IOException) {
        filesToCopy.forEach { source ->
            val target = if (source == legacyDatabase) {
                databaseFile
            } else {
                File(databaseFile.path + source.name.removePrefix(legacyDatabase.name))
            }
            try {
                Files.deleteIfExists(target.toPath())
            } catch (_: IOException) {
                // Keep the original failure as the actionable error.
            }
        }
        throw IllegalStateException(
            "Unable to preserve the existing JetHabit database from ${legacyDatabase.absolutePath}",
            exception,
        )
    }
}
