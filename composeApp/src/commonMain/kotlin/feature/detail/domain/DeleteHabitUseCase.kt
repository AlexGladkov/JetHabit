package feature.detail.domain

import feature.habits.data.HabitDao
import feature.habits.domain.HabitDeletedHook
import feature.habits.domain.NoOpHabitDeletedHook

class DeleteHabitUseCase(
    private val habitDao: HabitDao,
    private val deletedHook: HabitDeletedHook = NoOpHabitDeletedHook
) {

    suspend fun execute(habitId: String) {
        habitDao.deleteWith(habitId)
        // Habit deletion remains authoritative if reminder cleanup or platform cancellation fails.
        runCatching { deletedHook.onHabitDeleted(habitId) }
    }
}
