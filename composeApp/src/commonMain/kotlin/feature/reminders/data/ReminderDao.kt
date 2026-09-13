package feature.reminders.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(item: ReminderEntity)

    @Update
    suspend fun update(item: ReminderEntity)

    @Query("DELETE FROM ReminderEntity WHERE habitId = :habitId")
    suspend fun delete(habitId: String)

    @Query("SELECT * FROM ReminderEntity WHERE habitId = :habitId")
    fun getByHabitId(habitId: String): Flow<ReminderEntity?>
}
