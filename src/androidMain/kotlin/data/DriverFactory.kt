package data

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(private val context: Context) {
    actual suspend fun createDriver(schema: SqlDriver.Schema, name: String): SqlDriver {
        return AndroidSqliteDriver(schema, context, name)
    }
}