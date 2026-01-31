package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import feature.feed.domain.RecordStreakEventUseCase
import feature.habits.data.HabitDao
import kotlinx.datetime.LocalDate
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class SwitchHabitUseCase(
    private val dailyDao: DailyDao,
    private val habitDao: HabitDao,
    private val recordStreakEventUseCase: RecordStreakEventUseCase
) {

    suspend fun execute(checked: Boolean, habitId: String, date: LocalDate) {
        val habit = habitDao.getAll().first { it.id == habitId }

        // Always delete existing records first to ensure clean state
        dailyDao.deleteAllHabitsForToday(habitId, date.toString())

        // Create new record if checked, regardless of habit type
        if (checked) {
            dailyDao.insert(
                DailyEntity(
                    id = UUID.generateUUID().toString(),
                    habitId = habitId,
                    timestamp = date.toString(),
                    isChecked = true
                )
            )
        }

        // Record streak event in activity feed
        try {
            recordStreakEventUseCase.execute(habitId, date, checked)
        } catch (e: Exception) {
            // Silently fail if activity feed is not available
        }
    }
}