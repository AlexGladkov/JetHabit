package feature.feed.domain

import feature.daily.data.DailyDao
import feature.feed.data.ActivityFeedDao
import feature.feed.data.ActivityFeedEntity
import feature.feed.domain.model.ActivityFeedType
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.tracker.data.TrackerDao
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class DetectBrokenStreaksUseCase(
    private val habitDao: HabitDao,
    private val dailyDao: DailyDao,
    private val trackerDao: TrackerDao,
    private val activityFeedDao: ActivityFeedDao,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {

    suspend fun execute() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val habits = habitDao.getAll()

        for (habit in habits) {
            // Parse scheduled days
            val scheduledDays = parseDaysToCheck(habit.daysToCheck)
            if (scheduledDays.isEmpty()) {
                continue
            }

            // Get the latest feed entry for this habit
            val latestEntry = activityFeedDao.getLatestEntryForHabit(habit.id)

            // Determine the last date we checked
            val lastCheckedDate = latestEntry?.let { LocalDate.parse(it.date) }

            // Get all completion records for this habit
            val completionRecords = when (habit.type) {
                HabitType.REGULAR -> {
                    dailyDao.getAll()
                        .filter { it.habitId == habit.id && it.isChecked }
                        .map { it.timestamp }
                        .toSet()
                }
                HabitType.TRACKER -> {
                    trackerDao.getAll()
                        .filter { it.habitId == habit.id }
                        .map { it.timestamp }
                        .toSet()
                }
            }

            // Check for missed scheduled days since last check
            var checkDate = lastCheckedDate?.minus(1, DateTimeUnit.DAY) ?: today.minus(7, DateTimeUnit.DAY)
            var foundBrokenStreak = false

            // Walk backwards to find the first missed scheduled day
            while (checkDate >= today.minus(30, DateTimeUnit.DAY) && !foundBrokenStreak) {
                val dayOfWeek = checkDate.dayOfWeek.ordinal

                // Check if this day is scheduled
                if (scheduledDays.contains(dayOfWeek)) {
                    val dateString = checkDate.toString()

                    // If this scheduled day was missed
                    if (!completionRecords.contains(dateString)) {
                        // Check if we already have a broken streak entry for this date or nearby
                        val existingEntry = activityFeedDao.getLatestEntryForHabit(habit.id)
                        val shouldCreateEntry = existingEntry == null ||
                                existingEntry.type != ActivityFeedType.STREAK_BROKEN ||
                                LocalDate.parse(existingEntry.date) < checkDate

                        if (shouldCreateEntry) {
                            // Calculate the streak at the time of the break
                            // (this would be the streak before the missed day)
                            val streakBeforeBreak = calculateStreakUseCase.execute(habit.id, checkDate.minus(1, DateTimeUnit.DAY))

                            // Create a streak broken entry
                            activityFeedDao.insert(
                                ActivityFeedEntity(
                                    id = UUID.generateUUID().toString(),
                                    habitId = habit.id,
                                    habitTitle = habit.title,
                                    type = ActivityFeedType.STREAK_BROKEN,
                                    streakCount = streakBeforeBreak,
                                    date = checkDate.toString(),
                                    timestamp = checkDate.toEpochDays().toLong() * 86400000L
                                )
                            )

                            foundBrokenStreak = true
                        }
                    }
                }

                checkDate = checkDate.minus(1, DateTimeUnit.DAY)
            }
        }
    }

    private fun parseDaysToCheck(daysToCheckString: String): Set<Int> {
        return try {
            daysToCheckString
                .trim('[', ']')
                .split(',')
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
}
