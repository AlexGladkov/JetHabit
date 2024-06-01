package feature.habits.data

import androidx.room.*

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HabitEntity)
    
    @Query("SELECT * FROM HabitEntity")
    suspend fun getAll(): List<HabitEntity>
    
    @Query("SELECT * FROM HabitEntity WHERE id=:id")
    suspend fun getHabitWith(id: String): HabitEntity

    @Query("DELETE FROM HabitEntity")
    suspend fun clear()

    @Query("DELETE FROM HabitEntity WHERE id=:id")
    suspend fun deleteWith(id: String)
}