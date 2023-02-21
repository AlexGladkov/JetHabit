package data

import app.cash.sqldelight.db.SqlDriver
import di.PlatformConfiguration

expect class DriverFactory(platformConfiguration: PlatformConfiguration) {
    fun createDriver(name: String): SqlDriver
}