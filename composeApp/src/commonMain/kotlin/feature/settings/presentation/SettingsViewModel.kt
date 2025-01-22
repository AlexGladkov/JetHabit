package feature.settings.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.settings.domain.ClearAllHabitsUseCase
import feature.settings.presentation.models.SettingsAction
import feature.settings.presentation.models.SettingsEvent
import feature.settings.presentation.models.SettingsViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel: BaseViewModel<SettingsViewState, SettingsAction, SettingsEvent>(
    initialState = SettingsViewState()
) {
    
    private val clearAllHabitsUseCase = Inject.instance<ClearAllHabitsUseCase>()

    override fun obtainEvent(viewEvent: SettingsEvent) {
        when (viewEvent) {
            SettingsEvent.ClearAllQueries -> clearAllData()
            SettingsEvent.BackClicked -> viewAction = SettingsAction.NavigateBack
        }
    }
    
    private fun clearAllData() {
        viewModelScope.launch(Dispatchers.Default) {
            clearAllHabitsUseCase.execute()
        }
    }
}