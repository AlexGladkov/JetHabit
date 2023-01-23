package data

import Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import di.PlatformConfiguration

actual class DriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {
    actual fun createDriver(name: String): SqlDriver {
        return NativeSqliteDriver(Database.Schema, name)
    }
}