package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.sqljs.initSqlDriver
import kotlinx.coroutines.await
import kotlin.js.Promise

actual class DriverFactory {

    actual suspend fun createDriver(schema: SqlDriver.Schema, name: String): SqlDriver {
        return initSqlDriver(schema).await()
    }
}