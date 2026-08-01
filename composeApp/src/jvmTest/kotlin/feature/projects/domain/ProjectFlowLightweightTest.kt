package feature.projects.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.detail.domain.UpdateHabitUseCase
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.habits.data.HabitType
import feature.habits.data.Measurement
import feature.habits.domain.CreateHabitUseCase
import feature.projects.data.ProjectDao
import feature.projects.data.ProjectEntity
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val PROJECT_ID: String = "project-alpha"
private const val OTHER_PROJECT_ID: String = "project-beta"
private const val HABIT_ID: String = "habit-alpha"
private const val OTHER_HABIT_ID: String = "habit-beta"
private const val UNASSIGNED_HABIT_ID: String = "habit-unassigned"
private const val START_DATE: String = "2024-01-01"
private const val END_DATE: String = "2024-01-31"
private const val MONDAY_DATE: String = "2024-01-08"
private const val CHECKED_DAILY_ID: String = "daily-alpha"
private const val ALL_DAYS_TO_CHECK: String = "[0,1,2,3,4,5,6]"
private const val UPDATED_DAYS_TO_CHECK: String = "[0,1,2]"

internal class ProjectFlowLightweightTest {
    @Test
    fun createHabitPersistsAssignedProjectAndNullProject(): Unit = runBlocking {
        val habitDao = FakeHabitDao()
        val useCase = CreateHabitUseCase(habitDao)

        useCase.execute(
            title = "Assigned habit",
            isGood = true,
            type = HabitType.REGULAR,
            measurement = Measurement.KILOGRAMS,
            startDate = START_DATE,
            endDate = END_DATE,
            projectId = PROJECT_ID
        )
        useCase.execute(
            title = "Unassigned habit",
            isGood = true,
            type = HabitType.REGULAR,
            measurement = Measurement.KILOGRAMS,
            startDate = START_DATE,
            endDate = END_DATE,
            projectId = null
        )

        assertEquals(PROJECT_ID, habitDao.habits.first().projectId)
        assertNull(habitDao.habits.last().projectId)
    }

    @Test
    fun dailyUseCaseFiltersHabitsBySelectedProjectAndShowsAllWhenProjectIsNull(): Unit = runBlocking {
        val dailyDao = FakeDailyDao(
            entries = listOf(
                DailyEntity(
                    id = CHECKED_DAILY_ID,
                    habitId = HABIT_ID,
                    timestamp = MONDAY_DATE,
                    isChecked = true
                )
            )
        )
        val useCase = GetHabitsForTodayUseCase(
            habitDao = FakeHabitDao(
                initialHabits = mutableListOf(
                    habit(id = HABIT_ID, projectId = PROJECT_ID),
                    habit(id = OTHER_HABIT_ID, projectId = OTHER_PROJECT_ID),
                    habit(id = UNASSIGNED_HABIT_ID, projectId = null)
                )
            ),
            dailyDao = dailyDao,
            trackerDao = FakeTrackerDao()
        )

        val allHabits = useCase.execute(date = LocalDate.parse(MONDAY_DATE), projectId = null)
        val projectHabits = useCase.execute(date = LocalDate.parse(MONDAY_DATE), projectId = PROJECT_ID)

        assertEquals(listOf(HABIT_ID, OTHER_HABIT_ID, UNASSIGNED_HABIT_ID), allHabits.map { habit -> habit.id })
        assertEquals(listOf(HABIT_ID), projectHabits.map { habit -> habit.id })
        assertEquals(true, projectHabits.single().isChecked)
    }

    @Test
    fun updateHabitCanChangeProjectAndClearProject(): Unit = runBlocking {
        val habitDao = FakeHabitDao(initialHabits = mutableListOf(habit(id = HABIT_ID, projectId = PROJECT_ID)))
        val useCase = UpdateHabitUseCase(habitDao)

        useCase.execute(
            habitId = HABIT_ID,
            habitTitle = "Moved habit",
            startDate = LocalDate.parse(START_DATE),
            endDate = LocalDate.parse(END_DATE),
            daysToCheck = UPDATED_DAYS_TO_CHECK,
            isGood = false,
            projectId = OTHER_PROJECT_ID
        )
        useCase.execute(
            habitId = HABIT_ID,
            habitTitle = "Uncategorized habit",
            startDate = LocalDate.parse(START_DATE),
            endDate = LocalDate.parse(END_DATE),
            daysToCheck = UPDATED_DAYS_TO_CHECK,
            isGood = true,
            projectId = null
        )

        assertNull(habitDao.getHabitWith(HABIT_ID).projectId)
        assertEquals(listOf(OTHER_PROJECT_ID, null), habitDao.insertedHabits.map { habit -> habit.projectId })
    }

    @Test
    fun deletingProjectNullifiesAssignedHabitsSoTheyRemainVisibleWithoutProjectFilter(): Unit = runBlocking {
        val habitDao = FakeHabitDao(
            initialHabits = mutableListOf(
                habit(id = HABIT_ID, projectId = PROJECT_ID),
                habit(id = OTHER_HABIT_ID, projectId = OTHER_PROJECT_ID)
            )
        )
        val projectDao = FakeProjectDao()
        val deleteProjectUseCase = DeleteProjectUseCase(projectDao = projectDao, habitDao = habitDao)
        val dailyUseCase = GetHabitsForTodayUseCase(
            habitDao = habitDao,
            dailyDao = FakeDailyDao(),
            trackerDao = FakeTrackerDao()
        )

        deleteProjectUseCase.execute(PROJECT_ID)
        val visibleHabits = dailyUseCase.execute(date = LocalDate.parse(MONDAY_DATE), projectId = null)
        val deletedProjectHabits = dailyUseCase.execute(date = LocalDate.parse(MONDAY_DATE), projectId = PROJECT_ID)

        assertEquals(listOf(PROJECT_ID), habitDao.requestedProjectIds)
        assertEquals(listOf(PROJECT_ID), projectDao.deletedProjectIds)
        assertNull(habitDao.getHabitWith(HABIT_ID).projectId)
        assertEquals(listOf(HABIT_ID, OTHER_HABIT_ID), visibleHabits.map { habit -> habit.id }.sorted())
        assertEquals(emptyList(), deletedProjectHabits)
    }

    private fun habit(id: String, projectId: String?): HabitEntity {
        return HabitEntity(
            id = id,
            title = "Habit $id",
            isGood = true,
            startDate = START_DATE,
            endDate = END_DATE,
            daysToCheck = ALL_DAYS_TO_CHECK,
            type = HabitType.REGULAR,
            measurement = Measurement.KILOGRAMS,
            projectId = projectId
        )
    }

    private class FakeHabitDao(
        initialHabits: MutableList<HabitEntity> = mutableListOf()
    ) : HabitDao {
        val habits: MutableList<HabitEntity> = initialHabits
        val insertedHabits = mutableListOf<HabitEntity>()
        val requestedProjectIds = mutableListOf<String>()

        override suspend fun insert(item: HabitEntity) {
            habits.removeAll { habit -> habit.id == item.id }
            habits += item
            insertedHabits += item
        }

        override suspend fun getAll(): List<HabitEntity> {
            return habits
        }

        override suspend fun getHabitWith(id: String): HabitEntity {
            return habits.first { habit -> habit.id == id }
        }

        override suspend fun clear() {
            habits.clear()
        }

        override suspend fun deleteWith(id: String) {
            habits.removeAll { habit -> habit.id == id }
        }

        override suspend fun getByProject(projectId: String): List<HabitEntity> {
            requestedProjectIds += projectId
            return habits.filter { habit -> habit.projectId == projectId }
        }
    }

    private class FakeDailyDao(
        private val entries: List<DailyEntity> = emptyList()
    ) : DailyDao {
        override suspend fun insert(item: DailyEntity) = Unit

        override suspend fun getAll(): List<DailyEntity> {
            return entries
        }

        override suspend fun getDailyRecordWith(id: Long): DailyEntity {
            return entries.first { entry -> entry.id == id.toString() }
        }

        override suspend fun deleteAllHabitsForToday(habitId: String, timestamp: String) = Unit

        override suspend fun update(item: DailyEntity) = Unit

        override suspend fun clear() = Unit

        override suspend fun isHabitChecked(habitId: String, date: String): Boolean {
            return entries.any { entry -> entry.habitId == habitId && entry.timestamp == date && entry.isChecked }
        }

        override suspend fun wasDateEverChecked(habitId: String, date: String): Boolean {
            return entries.any { entry -> entry.habitId == habitId && entry.timestamp == date }
        }
    }

    private class FakeProjectDao : ProjectDao {
        val deletedProjectIds = mutableListOf<String>()

        override suspend fun insert(project: ProjectEntity) = Unit

        override suspend fun getAll(): List<ProjectEntity> {
            return emptyList()
        }

        override suspend fun getById(id: String): ProjectEntity? {
            return null
        }

        override suspend fun deleteById(id: String) {
            deletedProjectIds += id
        }
    }

    private class FakeTrackerDao : TrackerDao {
        override suspend fun insert(entity: TrackerEntity) = Unit

        override suspend fun getLatestValueFor(habitId: String): TrackerEntity? {
            return null
        }

        override suspend fun getValueForDate(habitId: String, date: String): TrackerEntity? {
            return null
        }

        override suspend fun getAllValuesFor(habitId: String): List<TrackerEntity> {
            return emptyList()
        }

        override suspend fun deleteAllValuesFor(habitId: String) = Unit

        override suspend fun clear() = Unit

        override suspend fun getAll(): List<TrackerEntity> {
            return emptyList()
        }

        override suspend fun getAllForHabit(habitId: String): List<TrackerEntity> {
            return emptyList()
        }

        override suspend fun delete(id: String) = Unit
    }
}
