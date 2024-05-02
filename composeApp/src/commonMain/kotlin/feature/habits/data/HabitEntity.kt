package feature.habits.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isGood: Boolean
)