package feature.feed.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activityFeedEntity: ActivityFeedEntity)

    @Query("SELECT * FROM activity_feed ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getActivityFeed(limit: Int = 50): List<ActivityFeedEntity>

    @Query("SELECT * FROM activity_feed ORDER BY timestamp DESC")
    fun getActivityFeedFlow(): Flow<List<ActivityFeedEntity>>

    @Query("SELECT * FROM activity_feed WHERE habitId = :habitId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEntryForHabit(habitId: String): ActivityFeedEntity?

    @Query("DELETE FROM activity_feed WHERE habitId = :habitId AND date = :date")
    suspend fun deleteEntriesForHabitOnDate(habitId: String, date: String)

    @Query("DELETE FROM activity_feed WHERE habitId = :habitId")
    suspend fun deleteEntriesForHabit(habitId: String)
}
