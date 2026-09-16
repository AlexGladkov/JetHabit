package feature.reminders.domain

import feature.reminders.data.ReminderDao
import feature.reminders.data.ReminderEntity
import feature.reminders.domain.schedule.ReminderScheduler
import feature.reminders.domain.schedule.ReminderScheduleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Reproduction of bug A2: AdaptiveReminderDeletedHook swallowed failures from
 * reminderDao.delete and scheduler.cancel via independent runCatching blocks.
 *
 * Post-fix contract: the hook returns Result<Unit> — a failing dao.delete or
 * scheduler.cancel must surface as Result.failure (never silent success), and a
 * failing scheduler.cancel after a successful dao.delete is compensated by
 * restoring the reminder row so DB and alarm stay consistent.
 */
class AdaptiveReminderDeletedHookReproTest {

    private class FakeReminderDao(startReminder: ReminderEntity?) : ReminderDao {
        val rows = MutableStateFlow(startReminder?.let { mapOf(it.habitId to it) } ?: emptyMap())
        var deleteError: Throwable? = null

        override suspend fun insert(item: ReminderEntity) { rows.value = rows.value + (item.habitId to item) }
        override suspend fun update(item: ReminderEntity) { rows.value = rows.value + (item.habitId to item) }
        override suspend fun delete(habitId: String) {
            deleteError?.let { throw it }
            rows.value = rows.value - habitId
        }
        override fun getByHabitId(habitId: String): Flow<ReminderEntity?> = rows.value[habitId].let { MutableStateFlow(it) }
        override suspend fun getSnapshot(habitId: String): ReminderEntity? = rows.value[habitId]
        override suspend fun advanceAnchorIfNewer(habitId: String, newAnchor: Long): Int = 0
    }

    private class FakeScheduler : ReminderScheduler {
        var cancelError: Throwable? = null
        val cancelled = mutableSetOf<String>()
        override suspend fun schedule(reminderId: String, triggerAt: kotlinx.datetime.Instant) =
            ReminderScheduleResult.Scheduled
        override suspend fun cancel(reminderId: String): ReminderScheduleResult {
            cancelError?.let { throw it }
            cancelled += reminderId
            return ReminderScheduleResult.Cancelled
        }
    }

    private fun entity(habitId: String) = ReminderEntity(
        habitId = habitId, hour = 9, minute = 0, daysMask = 0,
        timeZoneId = "UTC", adaptiveEnabled = true, anchor = null
    )

    @Test
    fun daoDeleteFailure_mustPropagate_notBeSwallowed() = runBlocking {
        val dao = FakeReminderDao(entity("h1")).apply { deleteError = RuntimeException("db write failed") }
        val scheduler = FakeScheduler()
        val hook = AdaptiveReminderDeletedHook(dao, scheduler, ReminderMutationLock())

        val result = hook.onHabitDeleted("h1")

        assertTrue(result.isFailure, "hook must surface the dao.delete failure, not report success")
        assertTrue(dao.getSnapshot("h1") != null, "reminder row must remain in DB when delete failed (no desync)")
        assertTrue(scheduler.cancelled.isEmpty(),
            "scheduler.cancel skipped after dao failure — alarm left untouched, consistent state")
    }

    @Test
    fun schedulerCancelFailure_mustPropagate_notBeSwallowed() = runBlocking {
        val dao = FakeReminderDao(entity("h2"))
        val scheduler = FakeScheduler().apply { cancelError = RuntimeException("alarm manager failed") }
        val hook = AdaptiveReminderDeletedHook(dao, scheduler, ReminderMutationLock())

        val result = hook.onHabitDeleted("h2")

        // Failure must surface (never silent success) ...
        assertTrue(result.isFailure, "hook must surface the scheduler.cancel failure, not report success")
        // ... and the DB must be compensated back into consistency with the still-active alarm.
        assertTrue(dao.getSnapshot("h2") != null,
            "reminder row must be restored (compensation) after failed cancel — no DB/alarm desync")
        assertTrue("h2" !in scheduler.cancelled,
            "cancel did fail; compensation re-inserted the row the alarm still points at")
    }
}
