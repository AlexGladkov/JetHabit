package screens.settings

import base.BaseViewModel
import screens.settings.models.SettingsAction
import screens.settings.models.SettingsEvent
import screens.settings.models.SettingsViewState

class SettingsViewModel: BaseViewModel<SettingsViewState, SettingsAction, SettingsEvent>(
    initialState = SettingsViewState()
) {

    override fun obtainEvent(viewEvent: SettingsEvent) {
        when (viewEvent) {
            else -> {}
        }
    }
}