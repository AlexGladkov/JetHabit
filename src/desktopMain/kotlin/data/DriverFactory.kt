package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
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