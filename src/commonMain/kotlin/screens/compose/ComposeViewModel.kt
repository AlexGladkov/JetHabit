package screens.compose

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.habit.HabitRepository
import di.Inject
import kotlinx.coroutines.launch
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState

class ComposeViewModel: BaseSharedViewModel<ComposeViewState, Unit, ComposeEvent>(
    initialState = ComposeViewState.ViewStateInitial()
) {

    private val habitRepository: HabitRepository = Inject.instance()

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

            viewState = try {
                habitRepository.addNewHabit(
                    title = state.habitTitle,
                    isGood = state.isGoodHabit
                )

                ComposeViewState.ViewStateSuccess
            } catch (e: Exception) {
                state.copy(isSending = false)
            }
        }
    }
}