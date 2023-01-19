package screens.compose

import com.adeo.kviewmodel.BaseSharedViewModel
import kotlinx.coroutines.launch
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState

//@HiltViewModel
class ComposeViewModel(

) : BaseSharedViewModel<ComposeViewState, Unit, ComposeEvent>(
    initialState = ComposeViewState.ViewStateInitial()
) {

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
            ComposeEvent.SaveClicked -> saveHabitToDatabase(currentState)
        }
    }

    private fun saveHabitToDatabase(state: ComposeViewState.ViewStateInitial) {
        viewModelScope.launch {
            viewState = state.copy(isSending = true)

//            try {
//                habitRepository.addNewHabit(
//                    HabitEntity(
//                        title = state.habitTitle,
//                        isGood = state.isGoodHabit
//                    )
//                )
//
//                _composeViewState.postValue(ComposeViewState.ViewStateSuccess)
//            } catch (e: Exception) {
//                _composeViewState.postValue(state.copy(isSending = false))
//            }
        }
    }
}