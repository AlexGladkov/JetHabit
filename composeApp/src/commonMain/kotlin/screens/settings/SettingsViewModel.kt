package screens.settings

import com.adeo.kviewmodel.BaseSharedViewModel
import screens.settings.models.SettingsAction
import screens.settings.models.SettingsEvent
import screens.settings.models.SettingsViewState

class SettingsViewModel: BaseSharedViewModel<SettingsViewState, SettingsAction, SettingsEvent>(
    initialState = SettingsViewState()
) {

    override fun obtainEvent(viewEvent: SettingsEvent) {
        when (viewEvent) {
            else -> {}
        }
    }
}