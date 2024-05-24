package screens.detail.models

sealed class DetailAction {
    object CloseScreen : DetailAction()
    object DateError : DetailAction()
}
