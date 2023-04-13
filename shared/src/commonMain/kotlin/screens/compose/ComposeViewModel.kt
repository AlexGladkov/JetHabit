package screens.compose

import com.adeo.kviewmodel.BaseSharedViewModel
import di.Inject
import kotlinx.coroutines.launch
import screens.compose.models.ComposeAction
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState

class ComposeViewModel: BaseSharedViewModel<ComposeViewState, ComposeAction, ComposeEvent>(
    initialState = ComposeViewState.ViewStateInitial()
) {

//    private val habitRepository: HabitRepository = Inject.instance()

    override fun obtainEvent(event: ComposeEvent) {
        when (viewState) {
            is ComposeViewState.ViewStateInitial -> reduce(event, viewState as ComposeViewState.ViewStateInitial)
            else -> {}
        }
    }

    private fun reduce(event: ComposeEvent, currentState: ComposeViewState.ViewStateInitial) {
        when (event) {
            is ComposeEvent.TitleChanged -> viewState = currentState.copy(habitTitle = event.newValue)
            is ComposeEvent.CheckboxClicked -> viewState = currentState.copy(isGoodHabit = event.newValue)
            is ComposeEvent.CloseClicked -> viewAction = ComposeAction.CloseScreen
            is ComposeEvent.ClearClicked -> viewState = currentState.copy(habitTitle = "")
            ComposeEvent.SaveClicked -> saveHabitToDatabase(currentState)
        }
    }

    private fun saveHabitToDatabase(state: ComposeViewState.ViewStateInitial) {
//        viewModelScope.launch {
//            viewState = state.copy(isSending = true)
//
//            viewState = try {
//                habitRepository.addNewHabit(
//                    title = applyiOSLowering(state.habitTitle),
//                    isGood = state.isGoodHabit
//                )
//
//                ComposeViewState.ViewStateSuccess
//            } catch (e: Exception) {
//                state.copy(isSending = false)
//            }
//        }
    }

    private fun applyiOSLowering(value: String): String {
        return value.toLowerCase().capitalize()
    }
}