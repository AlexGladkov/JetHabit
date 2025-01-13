package feature.tracker.data

import androidx.room.*

@Dao
interface TrackerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TrackerEntity)

    @Query("SELECT * FROM TrackerEntity WHERE habitId = :habitId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestValueFor(habitId: String): TrackerEntity?

    @Query("SELECT * FROM TrackerEntity WHERE habitId = :habitId AND timestamp = :date")
    suspend fun getValueForDate(habitId: String, date: String): TrackerEntity?

    @Query("SELECT * FROM TrackerEntity WHERE habitId = :habitId ORDER BY timestamp DESC")
    suspend fun getAllValuesFor(habitId: String): List<TrackerEntity>

    @Query("DELETE FROM TrackerEntity WHERE habitId = :habitId")
    suspend fun deleteAllValuesFor(habitId: String)

    @Query("DELETE FROM TrackerEntity")
    suspend fun clear()

    @Query("SELECT * FROM TrackerEntity")
    suspend fun getAll(): List<TrackerEntity>

    @Query("SELECT * FROM TrackerEntity WHERE habitId = :habitId ORDER BY timestamp DESC")
    suspend fun getAllForHabit(habitId: String): List<TrackerEntity>

    @Query("DELETE FROM TrackerEntity WHERE id = :id")
    suspend fun delete(id: String)
} 