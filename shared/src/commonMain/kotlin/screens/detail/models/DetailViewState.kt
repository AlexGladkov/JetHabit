package screens.detail.models

import tech.mobiledeveloper.shared.AppRes

data class DetailViewState(
    val itemTitle: String = "",
    val startDate: String = AppRes.string.not_selected,
    val endDate: String = AppRes.string.not_selected,
    val isGood: Boolean = false,
    val isDeleting: Boolean = false,
)