package feature.projects.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val colorHex: String
)
