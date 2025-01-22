package feature.settings.presentation.models

sealed class SettingsAction {
    data object NavigateBack : SettingsAction()
}