package feature.detail.presentation.models

import feature.habits.data.HabitType
import utils.CalendarDays
import kotlinx.datetime.LocalDate

enum class DateSelectionState {
    None, Start, End
}

data class DetailViewState(
    val habitId: String,
    val itemTitle: String = "",
    val startDate: CalendarDays = CalendarDays.Today,
    val endDate: CalendarDays = CalendarDays.Today,
    val start: LocalDate? = null,
    val end: LocalDate? = null,
    val daysToCheck: List<Int> = emptyList(),
    val isGood: Boolean = true,
    val isDeleting: Boolean = false,
    val dateSelectionState: DateSelectionState = DateSelectionState.None,
    val type: HabitType = HabitType.REGULAR,
    val currentValue: Double? = null,
    val newValue: Double? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: String? = null
)