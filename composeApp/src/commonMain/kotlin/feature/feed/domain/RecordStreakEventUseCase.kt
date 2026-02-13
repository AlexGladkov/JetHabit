package feature.feed.domain

import feature.feed.data.ActivityFeedDao
import feature.feed.data.ActivityFeedEntity
import feature.feed.domain.model.ActivityFeedType
import feature.habits.data.HabitDao
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class RecordStreakEventUseCase(
    private val habitDao: HabitDao,
    private val activityFeedDao: ActivityFeedDao,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {

    suspend fun execute(habitId: String, date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()), isChecked: Boolean) {
        val habit = habitDao.getAll().firstOrNull { it.id == habitId } ?: return

        // If unchecking, remove any feed entries for this date
        if (!isChecked) {
            activityFeedDao.deleteEntriesForHabitOnDate(habitId, date.toString())
            return
        }

        // Calculate current streak
        val currentStreak = calculateStreakUseCase.execute(habitId, date)

        if (currentStreak == 0) {
            // No streak to record
            return
        }

        // Check if we need to record a broken streak first
        val scheduledDays = parseDaysToCheck(habit.daysToCheck)
        if (scheduledDays.isNotEmpty()) {
            val previousScheduledDate = findPreviousScheduledDate(date, scheduledDays)
            if (previousScheduledDate != null) {
                // Calculate what the streak was before this check
                val previousStreak = calculateStreakUseCase.execute(habitId, previousScheduledDate)

                // If previous streak was 0 and current is 1, there might have been a break
                if (previousStreak == 0 && currentStreak >= 1) {
                    // Check if we already have a broken streak entry
                    val latestEntry = activityFeedDao.getLatestEntryForHabit(habitId)
                    if (latestEntry != null && latestEntry.type == ActivityFeedType.STREAK_INCREMENT) {
                        // There was a break - record it
                        val streakBeforeBreak = latestEntry.streakCount

                        activityFeedDao.insert(
                            ActivityFeedEntity(
                                id = UUID.generateUUID().toString(),
                                habitId = habitId,
                                habitTitle = habit.title,
                                type = ActivityFeedType.STREAK_BROKEN,
                                streakCount = streakBeforeBreak,
                                date = previousScheduledDate.toString(),
                                timestamp = previousScheduledDate.toEpochDays().toLong() * 86400000L
                            )
                        )
                    }
                }
            }
        }

        // Delete any existing entry for this date (to handle re-checks)
        activityFeedDao.deleteEntriesForHabitOnDate(habitId, date.toString())

        // Create a new streak increment entry
        activityFeedDao.insert(
            ActivityFeedEntity(
                id = UUID.generateUUID().toString(),
                habitId = habitId,
                habitTitle = habit.title,
                type = ActivityFeedType.STREAK_INCREMENT,
                streakCount = currentStreak,
                date = date.toString(),
                timestamp = date.toEpochDays().toLong() * 86400000L
            )
        )
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

    private fun findPreviousScheduledDate(currentDate: LocalDate, scheduledDays: Set<Int>): LocalDate? {
        var checkDate = currentDate.minus(1, DateTimeUnit.DAY)
        var daysChecked = 0

        while (daysChecked < 7) {
            val dayOfWeek = checkDate.dayOfWeek.ordinal
            if (scheduledDays.contains(dayOfWeek)) {
                return checkDate
            }
            checkDate = checkDate.minus(1, DateTimeUnit.DAY)
            daysChecked++
        }

        return null
    }
}
