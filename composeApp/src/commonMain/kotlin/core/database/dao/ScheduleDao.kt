package core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import core.database.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM ScheduleEntity WHERE habitId = :habitId")
    fun getSchedulesForHabit(habitId: String): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM ScheduleEntity WHERE isEnabled = 1")
    fun getEnabledSchedules(): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM ScheduleEntity WHERE id = :id")
    suspend fun deleteSchedule(id: String)

    @Query("DELETE FROM ScheduleEntity WHERE habitId = :habitId")
    suspend fun deleteSchedulesForHabit(habitId: String)
}
