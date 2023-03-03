package screens.detail.models

import kotlinx.datetime.LocalDateTime
import tech.mobiledeveloper.shared.AppRes

data class DetailViewState(
    val itemTitle: String = "",
    val startDate: String = AppRes.string.not_selected,
    val endDate: String = AppRes.string.not_selected,
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null,
    val isGood: Boolean = false,
    val isDeleting: Boolean = false,
)