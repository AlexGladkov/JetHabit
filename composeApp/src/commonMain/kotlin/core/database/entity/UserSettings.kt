package core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1, // Single user settings entry
    val isDarkMode: Boolean,
    val style: String, // JetHabitStyle enum name
    val textSize: String, // JetHabitSize enum name
    val paddingSize: String, // JetHabitSize enum name
    val cornerStyle: String // JetHabitCorners enum name
)
