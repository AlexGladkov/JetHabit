package feature.detail.presentation.models

import com.soywiz.klock.DateTime
import kotlinx.datetime.LocalDate
import tech.mobiledeveloper.jethabit.app.AppRes

enum class DateSelectionState {
    None, Start, End
}

data class DetailViewState(
    val itemTitle: String = "",
    val startDate: String = AppRes.string.not_selected,
    val endDate: String = AppRes.string.not_selected,
    val start: LocalDate? = null,
    val end: LocalDate? = null,
    val dateSelectionState: DateSelectionState = DateSelectionState.None,
    val isGood: Boolean = false,
    val isDeleting: Boolean = false,
)