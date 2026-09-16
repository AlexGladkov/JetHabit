package feature.detail.domain

import feature.habits.data.HabitDao
import feature.habits.domain.HabitDeletedHook
import feature.habits.domain.NoOpHabitDeletedHook

class DeleteHabitUseCase(
    private val habitDao: HabitDao,
    private val deletedHook: HabitDeletedHook = NoOpHabitDeletedHook
) {

    /**
     * Habit deletion remains authoritative; the post-delete hook outcome is retried once
     * (hook operations are idempotent) and then surfaced to the caller for logging/handling.
     */
    suspend fun execute(habitId: String): Result<Unit> {
        habitDao.deleteWith(habitId)
        val first = deletedHook.onHabitDeleted(habitId)
        if (first.isSuccess) return first
        return first.recoverCatching { deletedHook.onHabitDeleted(habitId).getOrThrow() }
    }
}
