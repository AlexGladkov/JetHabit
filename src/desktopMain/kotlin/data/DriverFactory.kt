package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import di.PlatformConfiguration

actual class DriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {

    actual fun createDriver(name: String): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:$name")
    }

}