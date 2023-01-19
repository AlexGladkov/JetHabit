package data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual class DriverFactory {

    actual suspend fun createDriver(schema: SqlDriver.Schema, name: String): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:$name")
    }

}