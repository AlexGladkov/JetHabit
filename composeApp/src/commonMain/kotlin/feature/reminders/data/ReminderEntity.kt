package feature.reminders.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ReminderEntity")
data class ReminderEntity(
    @PrimaryKey val habitId: String,
    val hour: Int,
    val minute: Int,
    val daysMask: Int,
    val timeZoneId: String,
    val adaptiveEnabled: Boolean = false,
    val anchor: Long? = null
)
