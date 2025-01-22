package feature.settings.presentation.models

sealed class SettingsEvent {
    data object ClearAllQueries : SettingsEvent()
    data object BackClicked : SettingsEvent()
}
