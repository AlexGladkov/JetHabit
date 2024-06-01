package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import kotlinx.datetime.LocalDate
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class SwitchHabitUseCase(
    private val dailyDao: DailyDao
) {

    suspend fun execute(checked: Boolean, habitId: String, date: LocalDate) {
        if (checked) {
            dailyDao.insert(
                DailyEntity(
                    id = UUID.generateUUID().toString(),
                    habitId = habitId,
                    timestamp = date.toString(),
                    isChecked = checked
                )
            )
        } else {
            dailyDao.deleteAllHabitsForToday(habitId, date.toString())
        }
    }
}