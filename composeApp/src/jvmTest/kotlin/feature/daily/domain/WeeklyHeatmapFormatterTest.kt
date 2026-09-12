package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitEntity
import feature.habits.data.HabitType
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** JVM fake of [DailyDao] backed by a mutable list. */
private class FakeDailyDao : DailyDao {
    val items = mutableListOf<DailyEntity>()

    override suspend fun insert(item: DailyEntity) { items += item }

    override suspend fun getAll(): List<DailyEntity> = items.toList()

    override suspend fun getDailyRecordWith(id: Long): DailyEntity =
        items.first { it.id == id.toString() }

    override suspend fun deleteAllHabitsForToday(habitId: String, timestamp: String) {
        items.removeAll { it.habitId == habitId && it.timestamp == timestamp }
    }

    override suspend fun update(item: DailyEntity) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) items[index] = item
    }

    override suspend fun clear() { items.clear() }

    override suspend fun isHabitChecked(habitId: String, date: String): Boolean =
        items.any { it.habitId == habitId && it.timestamp == date && it.isChecked }

    override suspend fun wasDateEverChecked(habitId: String, date: String): Boolean =
        items.any { it.habitId == habitId && it.timestamp == date }
}

private fun habit(id: String) = HabitEntity(
    id = id,
    title = "Habit $id",
    isGood = true,
    startDate = "2024-02-26",
    endDate = "2024-12-31",
    daysToCheck = "1111111",
    type = HabitType.REGULAR,
    projectId = null
)

class WeeklyHeatmapFormatterTest {

    private val endDate: LocalDate = LocalDate.parse("2024-05-19") // Sunday

    private fun formatter(dao: DailyDao) = WeeklyHeatmapFormatter(
        dailyDao = dao,
        dateProvider = { endDate }
    )

    private fun entity(
        habitId: String,
        timestamp: String,
        isChecked: Boolean,
        id: Long = 0
    ) = DailyEntity(id = id.toString(), habitId = habitId, timestamp = timestamp, isChecked = isChecked)

    @Test
    fun `grid is 7 rows by 12 weeks`() = runBlocking {
        val model = formatter(FakeDailyDao()).build(habits = listOf(habit("h1")))

        assertEquals(7, model.grid.size)
        assertTrue(model.grid.all { it.size == 12 })
        assertEquals(84, model.grid.sumOf { it.size })
    }

    @Test
    fun `all checked entries produce DONE status`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-19", true, 1))
        dao.insert(entity("h2", "2024-05-19", true, 2))

        val model = formatter(dao).build(habits = listOf(habit("h1"), habit("h2")))

        assertEquals(1, model.dayDetails.size)
        assertEquals(DayCellStatus.DONE, statusOf(model, LocalDate.parse("2024-05-19")))
    }

    @Test
    fun `some checked entries produce PARTIAL status`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-19", true, 1))
        dao.insert(entity("h2", "2024-05-19", false, 2))

        val model = formatter(dao).build(habits = listOf(habit("h1"), habit("h2")))

        assertEquals(DayCellStatus.PARTIAL, statusOf(model, LocalDate.parse("2024-05-19")))
    }

    @Test
    fun `no checked entries produce NONE status`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-19", false, 1))

        val model = formatter(dao).build(habits = listOf(habit("h1")))

        assertEquals(DayCellStatus.NONE, statusOf(model, LocalDate.parse("2024-05-19")))
    }

    @Test
    fun `days without records stay EMPTY`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-19", true, 1))

        val model = formatter(dao).build(habits = listOf(habit("h1")))

        assertEquals(DayCellStatus.EMPTY, statusOf(model, LocalDate.parse("2024-05-18")))
        assertEquals(DayCellStatus.EMPTY, statusOf(model, LocalDate.parse("2024-05-17")))
    }

    @Test
    fun `iso instant timestamps are parsed`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-18T10:30:00Z", true, 1))

        val model = formatter(dao).build(habits = listOf(habit("h1")))

        assertEquals(DayCellStatus.DONE, statusOf(model, LocalDate.parse("2024-05-18")))
    }

    @Test
    fun `broken timestamps are skipped`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "not-a-date", true, 1))
        dao.insert(entity("h2", "", true, 2))

        val model = formatter(dao).build(habits = listOf(habit("h1"), habit("h2")))

        assertTrue(model.dayDetails.isEmpty())
    }

    @Test
    fun `entries outside grid range are ignored`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-20", true, 1)) // after endDate
        dao.insert(entity("h1", "2024-02-01", true, 2)) // before grid start

        val model = formatter(dao).build(habits = listOf(habit("h1")))

        assertTrue(model.dayDetails.isEmpty())
    }

    @Test
    fun `empty dao produces empty model`() = runBlocking {
        val model = formatter(FakeDailyDao()).build(habits = listOf(habit("h1")))

        assertTrue(model.dayDetails.isEmpty())
        assertTrue(model.grid.flatten().all { it == DayCellStatus.EMPTY })
    }

    @Test
    fun `day details contain habit titles`() = runBlocking {
        val dao = FakeDailyDao()
        dao.insert(entity("h1", "2024-05-19", true, 1))

        val model = formatter(dao).build(habits = listOf(habit("h1")))

        val details = model.dayDetails.getValue(LocalDate.parse("2024-05-19"))
        assertEquals(1, details.entries.size)
        assertEquals("Habit h1", details.entries.first().habitTitle)
        assertTrue(details.entries.first().isChecked)
    }

    @Test
    fun `dateProvider is used when endDate omitted`() = runBlocking {
        var provided = endDate
        val formatter = WeeklyHeatmapFormatter(FakeDailyDao()) { provided }

        val model = formatter.build(habits = emptyList())
        assertEquals(endDate, model.endDate)

        provided = endDate.plus(3, DateTimeUnit.DAY)
        val shifted = formatter.build(habits = emptyList())
        assertEquals(endDate.plus(3, DateTimeUnit.DAY), shifted.endDate)
    }

    private fun statusOf(model: HeatmapModel, date: LocalDate): DayCellStatus {
        val gridStart = model.endDate
            .minus((model.weeks - 1) * 7, DateTimeUnit.DAY)
            .minus(model.endDate.dayOfWeek.ordinal, DateTimeUnit.DAY)
        val offset = date.toEpochDays() - gridStart.toEpochDays()
        val row = offset % 7
        val column = offset / 7
        return model.grid[row][column]
    }
}
