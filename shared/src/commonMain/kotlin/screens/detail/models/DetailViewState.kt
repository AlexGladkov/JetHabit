package screens.detail.models

import com.soywiz.klock.DateTime
import tech.mobiledeveloper.shared.AppRes

data class DetailViewState(
    val itemTitle: String = "",
    val startDate: String = AppRes.string.not_selected,
    val endDate: String = AppRes.string.not_selected,
    val start: DateTime? = null,
    val end: DateTime? = null,
    val isGood: Boolean = false,
    val isDeleting: Boolean = false,
)