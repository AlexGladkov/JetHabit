package feature.reminders.domain.schedule

import kotlinx.datetime.Instant

/** Platform delivery port. Recurrence and time-zone calculations stay in [ScheduleEngine]. */
interface ReminderScheduler {
    suspend fun schedule(reminderId: String, triggerAt: Instant): ReminderScheduleResult
    suspend fun cancel(reminderId: String): ReminderScheduleResult
}

sealed interface ReminderScheduleResult {
    data object Scheduled : ReminderScheduleResult
    data object Cancelled : ReminderScheduleResult
    data object PermissionDenied : ReminderScheduleResult
    data object Unsupported : ReminderScheduleResult
    data class Invalid(val reason: String) : ReminderScheduleResult
    data class Failed(val reason: String) : ReminderScheduleResult
}

class UnsupportedReminderScheduler : ReminderScheduler {
    override suspend fun schedule(reminderId: String, triggerAt: Instant) = ReminderScheduleResult.Unsupported
    override suspend fun cancel(reminderId: String) = ReminderScheduleResult.Unsupported
}
