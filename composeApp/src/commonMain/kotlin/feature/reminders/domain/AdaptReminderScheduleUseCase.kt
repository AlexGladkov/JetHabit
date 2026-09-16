package feature.reminders.domain

import feature.daily.data.DailyDao
import feature.habits.domain.HabitCheckedHook
import feature.habits.domain.HabitDeletedHook
import feature.reminders.data.ReminderDao
import feature.reminders.data.ReminderEntity
import feature.reminders.domain.schedule.Frequency
import feature.reminders.domain.schedule.ReminderConfig
import feature.reminders.domain.schedule.ReminderScheduleResult
import feature.reminders.domain.schedule.ReminderScheduler
import feature.reminders.domain.schedule.ScheduleEngine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Serializes reminder mutations so a delete always wins over an in-flight completion in this process. */
class ReminderMutationLock {
    private val mutex = Mutex()

    suspend fun <T> withLock(block: suspend () -> T): T = mutex.withLock { block() }
}

sealed interface AdaptReminderScheduleOutcome {
    enum class NoOpReason { MISSING_COMPLETION, MISSING_REMINDER, DISABLED, STALE_OR_DUPLICATE }

    data class NoOp(val reason: NoOpReason) : AdaptReminderScheduleOutcome
    data object Scheduled : AdaptReminderScheduleOutcome
    data object Cancelled : AdaptReminderScheduleOutcome
    data object PermissionDenied : AdaptReminderScheduleOutcome
    data object Unsupported : AdaptReminderScheduleOutcome
    data class Invalid(val reason: String) : AdaptReminderScheduleOutcome
    data class Failed(val reason: String) : AdaptReminderScheduleOutcome
}

class AdaptReminderScheduleUseCase(
    private val reminderDao: ReminderDao,
    private val dailyDao: DailyDao,
    private val engine: ScheduleEngine,
    private val scheduler: ReminderScheduler,
    private val now: () -> Instant = { Clock.System.now() },
    private val mutationLock: ReminderMutationLock = ReminderMutationLock()
) {
    suspend fun onHabitChecked(
        habitId: String,
        completedAt: Instant
    ): AdaptReminderScheduleOutcome = mutationLock.withLock {
        val completedAtEpochMs = completedAt.toEpochMilliseconds()
        if (dailyDao.getAll().none {
                it.habitId == habitId && it.isChecked && it.completedAtEpochMs == completedAtEpochMs
            }
        ) {
            return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.MISSING_COMPLETION
            )
        }

        val reminder = reminderDao.getSnapshot(habitId)
            ?: return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.MISSING_REMINDER
            )
        if (!reminder.adaptiveEnabled) {
            return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.DISABLED
            )
        }

        val advanced = reminderDao.advanceAnchorIfNewer(habitId, completedAtEpochMs)
        val persistedReminder = reminderDao.getSnapshot(habitId)
            ?: return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.MISSING_REMINDER
            )
        if (!persistedReminder.adaptiveEnabled) {
            return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.DISABLED
            )
        }

        val persistedAnchor = persistedReminder.anchor
            ?: return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.STALE_OR_DUPLICATE
            )
        if (advanced == 0 && persistedAnchor != completedAtEpochMs) {
            return@withLock AdaptReminderScheduleOutcome.NoOp(
                AdaptReminderScheduleOutcome.NoOpReason.STALE_OR_DUPLICATE
            )
        }

        // Replaying the same committed completion intentionally reconciles a prior delivery failure.
        val anchor = Instant.fromEpochMilliseconds(persistedAnchor)
        val config = try {
            persistedReminder.toConfig(anchor)
        } catch (failure: IllegalArgumentException) {
            return@withLock AdaptReminderScheduleOutcome.Invalid(failure.message.orEmpty())
        }
        val triggerAt = try {
            engine.nextOccurrence(config, anchor, now())
        } catch (failure: Throwable) {
            return@withLock AdaptReminderScheduleOutcome.Failed(failure.message ?: "schedule calculation failed")
        }

        val result = try {
            if (triggerAt == null) scheduler.cancel(habitId) else scheduler.schedule(habitId, triggerAt)
        } catch (failure: Throwable) {
            return@withLock AdaptReminderScheduleOutcome.Failed(failure.message ?: "scheduler failed")
        }
        result.toOutcome()
    }

    private fun ReminderEntity.toConfig(completedAt: Instant): ReminderConfig {
        require(hour in 0..23) { "hour out of range: $hour" }
        require(minute in 0..59) { "minute out of range: $minute" }
        require(daysMask and SUPPORTED_DAYS_MASK == daysMask) { "unsupported days mask: $daysMask" }
        val localDate = completedAt.toLocalDateTime(TimeZone.of(timeZoneId)).date
        val frequency = if (daysMask == 0) {
            Frequency.Daily
        } else {
            Frequency.Weekly(
                DAY_BITS.mapNotNullTo(mutableSetOf()) { (bit, day) ->
                    day.takeIf { daysMask and bit != 0 }
                }
            )
        }
        return ReminderConfig(
            anchor = LocalDateTime(localDate, LocalTime(hour, minute)),
            frequency = frequency,
            tzId = timeZoneId
        )
    }

    private fun ReminderScheduleResult.toOutcome(): AdaptReminderScheduleOutcome = when (this) {
        ReminderScheduleResult.Scheduled -> AdaptReminderScheduleOutcome.Scheduled
        ReminderScheduleResult.Cancelled -> AdaptReminderScheduleOutcome.Cancelled
        ReminderScheduleResult.PermissionDenied -> AdaptReminderScheduleOutcome.PermissionDenied
        ReminderScheduleResult.Unsupported -> AdaptReminderScheduleOutcome.Unsupported
        is ReminderScheduleResult.Invalid -> AdaptReminderScheduleOutcome.Invalid(reason)
        is ReminderScheduleResult.Failed -> AdaptReminderScheduleOutcome.Failed(reason)
    }

    private companion object {
        const val SUPPORTED_DAYS_MASK = 0x7f
        val DAY_BITS = listOf(
            1 to DayOfWeek.MONDAY,
            2 to DayOfWeek.TUESDAY,
            4 to DayOfWeek.WEDNESDAY,
            8 to DayOfWeek.THURSDAY,
            16 to DayOfWeek.FRIDAY,
            32 to DayOfWeek.SATURDAY,
            64 to DayOfWeek.SUNDAY
        )
    }
}

class AdaptiveReminderCheckedHook(
    private val adaptReminderSchedule: AdaptReminderScheduleUseCase
) : HabitCheckedHook {
    override suspend fun onHabitChecked(habitId: String, completedAt: Instant): Result<Unit> =
        try {
            when (val outcome = adaptReminderSchedule.onHabitChecked(habitId, completedAt)) {
                is AdaptReminderScheduleOutcome.Failed,
                is AdaptReminderScheduleOutcome.Invalid ->
                    Result.failure(IllegalStateException(outcome.toString()))
                else -> Result.success(Unit)
            }
        } catch (failure: Throwable) {
            Result.failure(failure)
        }
}

class AdaptiveReminderDeletedHook(
    private val reminderDao: ReminderDao,
    private val scheduler: ReminderScheduler,
    private val mutationLock: ReminderMutationLock
) : HabitDeletedHook {
    override suspend fun onHabitDeleted(habitId: String): Result<Unit> = mutationLock.withLock {
        val snapshot = reminderDao.getSnapshot(habitId)
        try {
            reminderDao.delete(habitId)
        } catch (failure: Throwable) {
            return@withLock Result.failure<Unit>(failure)
        }
        try {
            scheduler.cancel(habitId)
        } catch (failure: Throwable) {
            // Compensation: restore the reminder row so the DB stays consistent with the still-active alarm.
            snapshot?.let { runCatching { reminderDao.insert(it) } }
            return@withLock Result.failure<Unit>(failure)
        }
        Result.success(Unit)
    }
}
