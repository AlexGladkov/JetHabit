package feature.habits.domain

import kotlinx.datetime.Instant

/** Neutral post-commit hook owned by the composition root. */
fun interface HabitCheckedHook {
    suspend fun onHabitChecked(habitId: String, completedAt: Instant): Result<Unit>
}

/** Neutral post-delete hook owned by the composition root. */
fun interface HabitDeletedHook {
    suspend fun onHabitDeleted(habitId: String): Result<Unit>
}

object NoOpHabitCheckedHook : HabitCheckedHook {
    override suspend fun onHabitChecked(habitId: String, completedAt: Instant) = Result.success(Unit)
}

object NoOpHabitDeletedHook : HabitDeletedHook {
    override suspend fun onHabitDeleted(habitId: String) = Result.success(Unit)
}
