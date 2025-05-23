package feature.detail.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import utils.CalendarDays
import kotlinx.datetime.LocalDate

class HabitDetail(
    val habitTitle: String,
    val isHabitGood: Boolean,
    val startDate: CalendarDays,
    val endDate: CalendarDays,
    val start: LocalDate?,
    val end: LocalDate?,
    val daysToCheck: List<Int>,
    val type: HabitType
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

        val daysToCheck = habitEntity.daysToCheck
            .trim('[', ']') // Remove brackets
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }

        return HabitDetail(
            habitTitle = habitEntity.title,
            startDate = startDate,
            endDate = endDate,
            start = if (habitEntity.startDate.isEmpty()) null else LocalDate.parse(habitEntity.startDate) ,
            end = if (habitEntity.endDate.isEmpty()) null else LocalDate.parse(habitEntity.endDate),
            isHabitGood = habitEntity.isGood,
            daysToCheck = daysToCheck,
            type = habitEntity.type
        )
    }
}