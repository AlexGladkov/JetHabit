package screens.detail.models

import com.soywiz.klock.DateTime

sealed class DetailEvent {
    object DeleteItem : DetailEvent()
    object CloseScreen : DetailEvent()
    object SaveChanges : DetailEvent()
    object ActionInvoked : DetailEvent()
    data class StartDateSelected(val value: DateTime) : DetailEvent()
    data class EndDateSelected(val value: DateTime) : DetailEvent()
}