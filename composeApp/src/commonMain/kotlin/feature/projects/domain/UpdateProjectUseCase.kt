package feature.projects.domain

import feature.projects.data.ProjectDao
import feature.projects.data.ProjectEntity

class UpdateProjectUseCase(
    private val projectDao: ProjectDao
) {
    suspend fun execute(id: String, title: String, colorHex: String) {
        val project = ProjectEntity(
            id = id,
            title = title,
            colorHex = colorHex
        )
        projectDao.insert(project)
    }
}
