package feature.detail.presentation.models

import kotlinx.datetime.LocalDate

sealed class DetailEvent {
    data object DeleteItem : DetailEvent()
    data object CloseScreen : DetailEvent()
    data object SaveChanges : DetailEvent()
    data object StartDateClicked: DetailEvent()
    data object EndDateClicked : DetailEvent()
    class DateSelected(val value: LocalDate) : DetailEvent()
}