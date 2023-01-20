package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.sqljs.initSqlDriver
import di.PlatformConfiguration
import kotlinx.coroutines.await
import kotlin.js.Promise

actual class DriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {

    actual fun createDriver(name: String): SqlDriver {
        throw IllegalStateException("JS Not Implemented")
    }
}