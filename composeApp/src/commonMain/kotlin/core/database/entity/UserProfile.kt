package core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1, // Single user profile
    val name: String,
    val email: String,
    val phoneNumber: String,
    val avatarUri: String?
) 