package feature.detail.domain

import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import kotlinx.datetime.LocalDate

class UpdateHabitUseCase(
    private val habitDao: HabitDao
) {
    
    suspend fun execute(
        habitId: String,
        habitTitle: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        daysToCheck: String,
        isGood: Boolean,
        projectId: String? = null
    ) {
        if (startDate == null || endDate == null) throw NullPointerException("Date must not be null")
        if (startDate >= endDate) throw IllegalStateException("Start date must be before end date")
        if (habitTitle.isEmpty()) throw NullPointerException("Habit title must not be empty")

        val existingHabit = habitDao.getHabitWith(habitId)
        habitDao.insert(
            existingHabit.copy(
                title = habitTitle,
                startDate = startDate.toString(),
                endDate = endDate.toString(),
                daysToCheck = daysToCheck,
                isGood = isGood,
                projectId = projectId
            )
        )
    }
}