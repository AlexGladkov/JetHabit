package feature.create.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.habits.domain.CreateHabitUseCase
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import screens.compose.models.ComposeAction
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState

class ComposeViewModel: BaseViewModel<ComposeViewState, ComposeAction, ComposeEvent>(
    initialState = ComposeViewState()
) {

    private val createHabitUseCase = Inject.instance<CreateHabitUseCase>()
    private val json = Inject.instance<Json>()

    override fun obtainEvent(viewEvent: ComposeEvent) {
        when (viewEvent) {
            is ComposeEvent.CheckboxClicked -> viewState = viewState.copy(isGoodHabit = viewEvent.newValue)
            ComposeEvent.ClearClicked -> viewState = viewState.copy(habitTitle = "")
            ComposeEvent.CloseClicked -> viewAction = ComposeAction.CloseScreen
            ComposeEvent.SaveClicked -> saveHabitToDatabase()
            is ComposeEvent.TitleChanged -> viewState = viewState.copy(habitTitle = viewEvent.newValue)
        }
    }

    private fun saveHabitToDatabase() {
        viewModelScope.launch {
            viewState = viewState.copy(isSending = true)
            try {
                createHabitUseCase.execute(
                    title = viewState.habitTitle,
                    isGood = viewState.isGoodHabit,
                    days = json.encodeToJsonElement(listOf(1, 2, 3, 4, 5, 6, 7)).jsonArray
                )
                
                viewAction = ComposeAction.ShowSuccess
            } catch (e: Exception) {
                viewState = viewState.copy(isSending = false)
            }
        }
    }

    private fun applyiOSLowering(value: String): String {
        return value.toLowerCase().capitalize()
    }
}