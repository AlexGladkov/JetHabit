package feature.detail.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import utils.CalendarDays
import kotlinx.datetime.LocalDate

class HabitDetail(
    val habitTitle: String,
    val isHabitGood: Boolean,
    val startDate: CalendarDays,
    val endDeta: CalendarDays,
    val start: LocalDate?,
    val end: LocalDate?,
    val daysToCheck: String
)

class GetDetailInfoUseCase(
    private val habitDao: HabitDao
) {

    suspend fun execute(habitId: String): HabitDetail {
        val habitEntity = habitDao.getAll().first { it.id == habitId }

        val startDate = if (habitEntity.startDate.isEmpty()) {
            CalendarDays.NotSelected
        } else {
            CalendarDays.Custom(habitEntity.startDate)
        }

        val endDate = if (habitEntity.endDate.isEmpty()) {
            CalendarDays.NotSelected
        } else {
            CalendarDays.Custom(habitEntity.endDate)
        }

        return HabitDetail(
            habitTitle = habitEntity.title,
            startDate = startDate,
            endDeta = endDate,
            start = LocalDate.parse(habitEntity.startDate),
            end = LocalDate.parse(habitEntity.endDate),
            isHabitGood = habitEntity.isGood,
            daysToCheck = habitEntity.daysToCheck
        )
    }
}