package feature.daily.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyDao {
    @Insert
    suspend fun insert(item: DailyEntity)

    @Query("SELECT * FROM DailyEntity")
    suspend fun getAll(): List<DailyEntity>

    @Query("SELECT * FROM DailyEntity WHERE id=:id")
    fun getDailyRecordWith(id: Long): DailyEntity

    @Update
    suspend fun update(item: DailyEntity)
}
