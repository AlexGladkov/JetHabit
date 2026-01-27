package feature.projects.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity)

    @Query("SELECT * FROM ProjectEntity ORDER BY title ASC")
    suspend fun getAll(): List<ProjectEntity>

    @Query("SELECT * FROM ProjectEntity WHERE id = :id")
    suspend fun getById(id: String): ProjectEntity?

    @Query("DELETE FROM ProjectEntity WHERE id = :id")
    suspend fun deleteById(id: String)
}
