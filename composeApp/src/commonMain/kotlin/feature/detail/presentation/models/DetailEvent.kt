package feature.detail.presentation.models

import com.soywiz.klock.DateTime

sealed class DetailEvent {
    data object DeleteItem : DetailEvent()
    data object CloseScreen : DetailEvent()
    data object SaveChanges : DetailEvent()
    data object StartDateClicked: DetailEvent()
    data object EndDateClicked : DetailEvent()
    data class StartDateSelected(val value: DateTime) : DetailEvent()
    data class EndDateSelected(val value: DateTime) : DetailEvent()
}