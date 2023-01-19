package data

import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory(platformConfiguration: PlatformConfiguration) {
    suspend fun createDriver(schema: SqlDriver.Schema, name: String): SqlDriver
}