package feature.habits.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(item: HabitEntity)
    
    @Query("SELECT * FROM HabitEntity")
    suspend fun getAll(): List<HabitEntity>
    
    @Query("SELECT * FROM HabitEntity WHERE id=:id")
    fun getHabitWith(id: Long): HabitEntity

    @Query("DELETE FROM HabitEntity")
    fun clear()
}