package feature.habits.domain

import kotlinx.datetime.Instant

/** Neutral post-commit hook owned by the composition root. */
fun interface HabitCheckedHook {
    suspend fun onHabitChecked(habitId: String, completedAt: Instant)
}

/** Neutral post-delete hook owned by the composition root. */
fun interface HabitDeletedHook {
    suspend fun onHabitDeleted(habitId: String)
}

object NoOpHabitCheckedHook : HabitCheckedHook {
    override suspend fun onHabitChecked(habitId: String, completedAt: Instant) = Unit
}

object NoOpHabitDeletedHook : HabitDeletedHook {
    override suspend fun onHabitDeleted(habitId: String) = Unit
}
