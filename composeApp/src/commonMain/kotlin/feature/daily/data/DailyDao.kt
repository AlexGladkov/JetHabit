package feature.daily.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(item: DailyEntity)

    @Query("SELECT * FROM DailyEntity")
    suspend fun getAll(): List<DailyEntity>

    @Query("SELECT * FROM DailyEntity WHERE id=:id")
    suspend fun getDailyRecordWith(id: Long): DailyEntity

    @Query("DELETE FROM DailyEntity WHERE habitId=:habitId AND timestamp=:timestamp")
    suspend fun deleteAllHabitsForToday(habitId: String, timestamp: String)

    @Update
    suspend fun update(item: DailyEntity)

    @Query("DELETE FROM DailyEntity")
    suspend fun clear()

    @Query("SELECT EXISTS(SELECT 1 FROM DailyEntity WHERE habitId = :habitId AND timestamp = :date AND isChecked = 1)")
    suspend fun isHabitChecked(habitId: String, date: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM DailyEntity WHERE habitId = :habitId AND timestamp = :date)")
    suspend fun wasDateEverChecked(habitId: String, date: String): Boolean
}
