package feature.daily.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val timestamp: String,
    val isChecked: Boolean
)
