package feature.settings.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.settings.domain.ClearAllHabitsUseCase
import feature.settings.presentation.models.SettingsEvent
import feature.settings.presentation.models.SettingsViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class SettingsComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val clearAllHabitsUseCase: ClearAllHabitsUseCase by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(SettingsViewState())
    val state: Value<SettingsViewState> = _state

    fun onEvent(viewEvent: SettingsEvent) {
        when (viewEvent) {
            SettingsEvent.ClearAllQueries -> clearAllData()
            SettingsEvent.BackClicked -> onNavigateBack()
        }
    }

    private fun clearAllData() {
        scope.launch(Dispatchers.Default) {
            clearAllHabitsUseCase.execute()
        }
    }
}
