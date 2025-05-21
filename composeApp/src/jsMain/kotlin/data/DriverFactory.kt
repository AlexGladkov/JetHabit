package data

import com.squareup.sqldelight.db.SqlDriver
import di.PlatformConfiguration

actual class DriverFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {

    actual fun createDriver(name: String): SqlDriver {
        throw IllegalStateException("JS Not Implemented")
    }
}