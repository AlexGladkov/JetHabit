package feature.projects.domain

import feature.projects.data.ProjectDao
import feature.projects.data.ProjectEntity
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class CreateProjectUseCase(
    private val projectDao: ProjectDao
) {
    suspend fun execute(title: String, colorHex: String) {
        val project = ProjectEntity(
            id = UUID.generateUUID().toString(),
            title = title,
            colorHex = colorHex
        )
        projectDao.insert(project)
    }
}
