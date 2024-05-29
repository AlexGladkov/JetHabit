package feature.daily.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val timestamp: String,
    val isChecked: Boolean
)
