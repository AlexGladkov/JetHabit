package feature.habits.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HabitType {
    REGULAR,
    TRACKER
}

@Entity
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isGood: Boolean,
    val startDate: String,
    val endDate: String,
    val daysToCheck: String,
    val type: HabitType = HabitType.REGULAR,
    val measurement: Measurement = Measurement.KILOGRAMS
)