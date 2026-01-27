package feature.projects.domain

import feature.projects.data.ProjectDao
import feature.projects.data.ProjectEntity

class GetAllProjectsUseCase(
    private val projectDao: ProjectDao
) {
    suspend fun execute(): List<ProjectEntity> {
        return projectDao.getAll()
    }
}
