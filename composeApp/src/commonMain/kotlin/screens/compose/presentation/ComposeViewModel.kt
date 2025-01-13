package screens.compose.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.create.presentation.models.ComposeEvent
import feature.habits.data.HabitType
import feature.habits.domain.CreateHabitUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import screens.compose.models.ComposeAction
import screens.compose.models.ComposeViewState

class ComposeViewModel: BaseViewModel<ComposeViewState, ComposeAction, ComposeEvent>(
    initialState = ComposeViewState()
) {
    private val createHabitUseCase = Inject.instance<CreateHabitUseCase>()

    override fun obtainEvent(viewEvent: ComposeEvent) {
        when (viewEvent) {
            is ComposeEvent.TitleChanged -> viewState = viewState.copy(habitTitle = viewEvent.title)
            is ComposeEvent.CheckboxClicked -> viewState = viewState.copy(isGoodHabit = viewEvent.isChecked)
            is ComposeEvent.TypeSelected -> handleTypeSelection(viewEvent.type)
            is ComposeEvent.MeasurementSelected -> viewState = viewState.copy(measurement = viewEvent.measurement)
            ComposeEvent.SaveClicked -> createNewHabit()
            ComposeEvent.ClearClicked -> viewState = viewState.copy(habitTitle = "")
            ComposeEvent.CloseClicked -> viewAction = ComposeAction.CloseScreen
            is ComposeEvent.EndDateSelected -> TODO()
            ComposeEvent.HideEndDatePicker -> TODO()
            ComposeEvent.HideStartDatePicker -> TODO()
            ComposeEvent.ShowEndDatePicker -> TODO()
            ComposeEvent.ShowStartDatePicker -> TODO()
            is ComposeEvent.StartDateSelected -> TODO()
        }
    }

    private fun handleTypeSelection(newType: HabitType) {
        viewState = viewState.copy(
            habitType = newType
        )
    }

    private fun createNewHabit() {
        if (viewState.habitTitle.isBlank()) return

        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(isSending = true)
            }

            try {
                createHabitUseCase.execute(
                    title = viewState.habitTitle,
                    isGood = viewState.isGoodHabit,
                    type = viewState.habitType,
                    measurement = viewState.measurement
                )

                withContext(Dispatchers.Main) {
                    viewAction = ComposeAction.Success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewAction = ComposeAction.Error
                }
            }
        }
    }
} 