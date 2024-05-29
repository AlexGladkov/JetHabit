package feature.detail.presentation.models

sealed class DetailAction {
    data object CloseScreen : DetailAction()
    data object DateError : DetailAction()
}
