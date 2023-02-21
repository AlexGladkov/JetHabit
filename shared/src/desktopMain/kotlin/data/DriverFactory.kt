package data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import di.PlatformConfiguration
import java.io.File

actual class DriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {

    actual fun createDriver(name: String): SqlDriver {
        val appPath = platformConfiguration.appDataPath
        if (!File(appPath).exists()) {
            File(appPath).mkdirs()
        }

        val filePath = "$appPath/$name"
        return JdbcSqliteDriver("jdbc:sqlite:$filePath").apply {
            if (!File(filePath).exists()) {
                Database.Schema.create(this)
            }
        }
    }

}