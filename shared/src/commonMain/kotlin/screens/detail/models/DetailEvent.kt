package screens.detail.models

sealed class DetailEvent {
    object DeleteItem : DetailEvent()
    object CloseScreen : DetailEvent()
}