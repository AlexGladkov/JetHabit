package feature.projects.domain

import feature.habits.data.HabitDao
import feature.projects.data.ProjectDao

class DeleteProjectUseCase(
    private val projectDao: ProjectDao,
    private val habitDao: HabitDao
) {
    suspend fun execute(projectId: String) {
        // First, get all habits belonging to this project and update them to have null projectId
        val habits = habitDao.getByProject(projectId)
        habits.forEach { habit ->
            habitDao.insert(habit.copy(projectId = null))
        }

        // Then delete the project
        projectDao.deleteById(projectId)
    }
}
