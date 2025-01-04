package feature.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackerEntity(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val timestamp: String,
    val value: Double
) 