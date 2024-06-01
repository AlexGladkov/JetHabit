package feature.habits.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HabitEntity(
    @PrimaryKey var id: String,
    var title: String,
    var daysToCheck: String,
    var isGood: Boolean,
    var startDate: String,
    var endDate: String
)