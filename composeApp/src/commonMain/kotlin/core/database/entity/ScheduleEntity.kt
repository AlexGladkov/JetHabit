package core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val timeOfDay: String,
    val daysOfWeek: String,
    val isEnabled: Boolean = true,
    val lastFiredAt: Long? = null
)
