package feature.projects.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.projects.data.ProjectDao
import feature.projects.data.ProjectEntity
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProjectUseCaseTest {
    @Test
    fun createProjectInsertsProjectWithGeneratedIdAndProvidedFields(): Unit = runBlocking {
        val projectDao = FakeProjectDao()
        val useCase = CreateProjectUseCase(projectDao)

        useCase.execute(title = "Fitness", colorHex = "#FF0000")

        val project = projectDao.insertedProjects.single()
        assertNotNull(project.id)
        assertEquals(expected = "Fitness", actual = project.title)
        assertEquals(expected = "#FF0000", actual = project.colorHex)
    }

    @Test
    fun updateProjectReplacesProjectWithProvidedFields(): Unit = runBlocking {
        val projectDao = FakeProjectDao()
        val useCase = UpdateProjectUseCase(projectDao)

        useCase.execute(id = "project-id", title = "Updated", colorHex = "#00FF00")

        assertEquals(
            expected = ProjectEntity(id = "project-id", title = "Updated", colorHex = "#00FF00"),
            actual = projectDao.insertedProjects.single()
        )
    }

    @Test
    fun deleteProjectNullifiesHabitProjectIdsBeforeDeletingProject(): Unit = runBlocking {
        val projectDao = FakeProjectDao()
        val habitDao = FakeHabitDao(
            habits = listOf(
                habit(id = "habit-one", projectId = "project-id"),
                habit(id = "habit-two", projectId = "project-id")
            )
        )
        val useCase = DeleteProjectUseCase(projectDao = projectDao, habitDao = habitDao)

        useCase.execute(projectId = "project-id")

        assertEquals(expected = listOf("project-id"), actual = habitDao.requestedProjectIds)
        assertEquals(expected = "project-id", actual = projectDao.deletedProjectIds.single())
        assertEquals(expected = listOf("habit-one", "habit-two"), actual = habitDao.insertedHabits.map { habit -> habit.id })
        habitDao.insertedHabits.forEach { updatedHabit ->
            assertNull(updatedHabit.projectId)
        }
    }

    @Test
    fun deleteProjectDeletesProjectWhenNoHabitsAreAssigned(): Unit = runBlocking {
        val projectDao = FakeProjectDao()
        val habitDao = FakeHabitDao(habits = emptyList())
        val useCase = DeleteProjectUseCase(projectDao = projectDao, habitDao = habitDao)

        useCase.execute(projectId = "empty-project")

        assertEquals(expected = "empty-project", actual = projectDao.deletedProjectIds.single())
        assertEquals(expected = emptyList(), actual = habitDao.insertedHabits)
    }

    private fun habit(id: String, projectId: String?): HabitEntity {
        return HabitEntity(
            id = id,
            title = "Habit $id",
            isGood = true,
            startDate = "2024-01-01",
            endDate = "2024-01-31",
            daysToCheck = "1,2,3",
            projectId = projectId
        )
    }

    private class FakeProjectDao : ProjectDao {
        val insertedProjects = mutableListOf<ProjectEntity>()
        val deletedProjectIds = mutableListOf<String>()

        override suspend fun insert(project: ProjectEntity) {
            insertedProjects += project
        }

        override suspend fun getAll(): List<ProjectEntity> {
            return insertedProjects.sortedBy { project -> project.title }
        }

        override suspend fun getById(id: String): ProjectEntity? {
            return insertedProjects.firstOrNull { project -> project.id == id }
        }

        override suspend fun deleteById(id: String) {
            deletedProjectIds += id
        }
    }

    private class FakeHabitDao(
        private val habits: List<HabitEntity>
    ) : HabitDao {
        val insertedHabits = mutableListOf<HabitEntity>()
        val requestedProjectIds = mutableListOf<String>()

        override suspend fun insert(item: HabitEntity) {
            insertedHabits += item
        }

        override suspend fun getAll(): List<HabitEntity> {
            return habits
        }

        override suspend fun getHabitWith(id: String): HabitEntity {
            return habits.first { habit -> habit.id == id }
        }

        override suspend fun clear() = Unit

        override suspend fun deleteWith(id: String) = Unit

        override suspend fun getByProject(projectId: String): List<HabitEntity> {
            requestedProjectIds += projectId
            return habits.filter { habit -> habit.projectId == projectId }
        }
    }
}
