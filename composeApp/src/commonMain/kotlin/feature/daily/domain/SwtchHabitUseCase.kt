package feature.daily.domain

import feature.daily.data.DailyDao
import feature.daily.data.DailyEntity
import kotlinx.datetime.LocalDate

class SwitchHabitUseCase(
    private val dailyDao: DailyDao
) {

    suspend fun execute(checked: Boolean, habitId: Long, date: LocalDate) {
        if (checked) {
            dailyDao.insert(
                DailyEntity(
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