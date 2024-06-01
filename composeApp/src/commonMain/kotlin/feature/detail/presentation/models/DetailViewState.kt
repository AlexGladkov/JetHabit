package feature.detail.presentation.models

import com.soywiz.klock.DateTime
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.not_selected
import utils.CalendarDays

enum class DateSelectionState {
    None, Start, End
}

data class DetailViewState(
    val habitId: String,
    val itemTitle: String = "",
    val startDate: CalendarDays = CalendarDays.NotSelected,
    val endDate: CalendarDays = CalendarDays.NotSelected,
    val start: LocalDate? = null,
    val end: LocalDate? = null,
    val dateSelectionState: DateSelectionState = DateSelectionState.None,
    val isGood: Boolean = false,
    val isDeleting: Boolean = false,
    val daysToCheck: String = ""
)