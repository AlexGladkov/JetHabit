package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.habits.data.HabitDao
import feature.habits.domain.HabitCheckedHook
import feature.habits.domain.NoOpHabitCheckedHook
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class SwitchHabitUseCase(
    private val dailyDao: DailyDao,
    private val habitDao: HabitDao,
    private val checkedHook: HabitCheckedHook = NoOpHabitCheckedHook,
    private val now: () -> Instant = { Clock.System.now() }
) {

    suspend fun execute(
        checked: Boolean,
        habitId: String,
        date: LocalDate,
        completedAt: Instant = now()
    ) {
        habitDao.getAll().first { it.id == habitId }

        if (checked && dailyDao.isHabitChecked(habitId, date.toString())) return

        dailyDao.deleteAllHabitsForToday(habitId, date.toString())
        if (!checked) return

        dailyDao.insert(
            DailyEntity(
                id = UUID.generateUUID().toString(),
                habitId = habitId,
                timestamp = date.toString(),
                isChecked = true,
                completedAtEpochMs = completedAt.toEpochMilliseconds()
            )
        )

        // Completion is authoritative: post-commit feedback must never fail the checked mutation.
        runCatching { checkedHook.onHabitChecked(habitId, completedAt) }
    }
}
