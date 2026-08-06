package feature.feed.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import feature.feed.domain.model.ActivityFeedType

@Entity(tableName = "activity_feed")
data class ActivityFeedEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val habitTitle: String,
    val type: ActivityFeedType,
    val streakCount: Int,
    val date: String,
    val timestamp: Long
)
