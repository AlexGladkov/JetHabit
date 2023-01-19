package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DriverFactory() {
    actual suspend fun createDriver(schema: SqlDriver.Schema, name: String): SqlDriver {
        return NativeSqliteDriver(schema, name)
    }
}