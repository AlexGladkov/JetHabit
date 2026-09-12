package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Status of a single heatmap cell for a concrete date. */
enum class DayCellStatus {
    /** All recorded habits for the day are checked. */
    DONE,

    /** Some recorded habits for the day are checked. */
    PARTIAL,

    /** There are records for the day but none checked. */
    NONE,

    /** No records for the day (or the day is in the future relative to endDate). */
    EMPTY
}

/** Per-habit record of one day, used by the day details dialog. */
data class HeatmapDayEntry(
    val habitId: String,
    val habitTitle: String,
    val isChecked: Boolean
)

data class HeatmapDayDetails(
    val date: LocalDate,
    val entries: List<HeatmapDayEntry>
)

/**
 * Heatmap model: 7 rows (Mon..Sun) x [weeks] columns.
 * grid[row][week] — status of the cell; future dates are [DayCellStatus.EMPTY].
 */
data class HeatmapModel(
    val weeks: Int,
    val grid: List<List<DayCellStatus>>,
    val dayDetails: Map<LocalDate, HeatmapDayDetails>,
    val endDate: LocalDate
)

/**
 * Pure Kotlin formatter: builds a 7x12 habit heatmap from DailyDao records.
 * No Compose dependencies — safe to unit test on JVM.
 *
 * Timestamps are parsed defensively: both plain `LocalDate` strings ("2024-05-01")
 * and ISO-Instant strings ("2024-05-01T10:00:00Z") are supported; broken values are skipped.
 */
class WeeklyHeatmapFormatter(
    private val dailyDao: DailyDao,
    private val dateProvider: () -> LocalDate
) {
    suspend fun build(
        habits: List<HabitEntity>,
        endDate: LocalDate = dateProvider()
    ): HeatmapModel {
        val habitIds = habits.map { it.id }.toSet()
        val habitTitlesById = habits.associate { it.id to it.title }

        // 12 weeks grid ending in the week of endDate; first column starts on Monday.
        // Grid start = Monday of (endDate - 11 weeks), computed via epoch days.
        val gridStartEpoch = alignToMondayEpoch(
            endDate.toEpochDays() - (WEEKS - 1) * 7
        )

        val entriesInRange = dailyDao.getAll()
            .asSequence()
            .mapNotNull { entry -> parseTimestamp(entry.timestamp)?.let { entry to it } }
            .filter { (_, date) -> date.toEpochDays() >= gridStartEpoch && date <= endDate }
            .filter { (entry, _) -> habitIds.isEmpty() || entry.habitId in habitIds }
            .groupBy({ (_, date) -> date }, { (entry, _) -> entry })

        val grid = (0 until DAYS_PER_WEEK).map { dayIndex ->
            (0 until WEEKS).map { weekIndex ->
                val date = LocalDate.fromEpochDays(gridStartEpoch + weekIndex * 7 + dayIndex)
                statusFor(date, entriesInRange[date])
            }
        }

        val details = entriesInRange.mapValues { (date, dayEntries) ->
            HeatmapDayDetails(
                date = date,
                entries = dayEntries.map { entry ->
                    HeatmapDayEntry(
                        habitId = entry.habitId,
                        habitTitle = habitTitlesById[entry.habitId] ?: entry.habitId,
                        isChecked = entry.isChecked
                    )
                }
            )
        }

        return HeatmapModel(
            weeks = WEEKS,
            grid = grid,
            dayDetails = details,
            endDate = endDate
        )
    }

    private fun statusFor(date: LocalDate, dayEntries: List<DailyEntity>?): DayCellStatus {
        if (dayEntries.isNullOrEmpty()) return DayCellStatus.EMPTY
        val checked = dayEntries.count { it.isChecked }
        return when {
            checked == dayEntries.size -> DayCellStatus.DONE
            checked == 0 -> DayCellStatus.NONE
            else -> DayCellStatus.PARTIAL
        }
    }

    /** Shifts an epoch-days value back to the Monday of its week. */
    private fun alignToMondayEpoch(epochDays: Int): Int {
        val date = LocalDate.fromEpochDays(epochDays)
        return epochDays - date.dayOfWeek.ordinal // Monday ordinal = 0
    }

    private fun parseTimestamp(timestamp: String): LocalDate? {
        if (timestamp.isBlank()) return null
        // Plain LocalDate format first (the common storage format).
        runCatching { return LocalDate.parse(timestamp) }
        // ISO-Instant format fallback ("2024-05-01T10:00:00Z").
        return runCatching {
            Instant.parse(timestamp).toLocalDateTime(TimeZone.UTC).date
        }.getOrNull()
    }

    private companion object {
        const val WEEKS = 12
        const val DAYS_PER_WEEK = 7
    }
}
