package feature.daily.presentation.models

import feature.habits.data.HabitType

class DailyHabit(
    val id: String,
    val title: String,
    val isChecked: Boolean,
    val type: HabitType = HabitType.REGULAR,
    val value: Double? = null
)