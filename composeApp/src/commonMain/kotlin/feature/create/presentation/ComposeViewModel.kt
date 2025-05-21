package feature.create.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.create.presentation.models.ComposeEvent
import feature.create.presentation.models.ComposeViewState
import feature.habits.domain.CreateHabitUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import screens.compose.models.ComposeAction

class ComposeViewModel : BaseViewModel<ComposeViewState, ComposeAction, ComposeEvent>(
    initialState = ComposeViewState(
        startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(30, DateTimeUnit.DAY)
    )
) {
    private val createHabitUseCase: CreateHabitUseCase = Inject.instance()

    override fun obtainEvent(viewEvent: ComposeEvent) {
        when (viewEvent) {
            is ComposeEvent.TitleChanged -> viewState = viewState.copy(habitTitle = viewEvent.title)
            is ComposeEvent.CheckboxClicked -> viewState = viewState.copy(isGoodHabit = viewEvent.isChecked)
            is ComposeEvent.TypeSelected -> viewState = viewState.copy(habitType = viewEvent.type)
            is ComposeEvent.MeasurementSelected -> viewState = viewState.copy(measurement = viewEvent.measurement)
            is ComposeEvent.StartDateSelected -> {
                if (viewEvent.date <= (viewState.endDate ?: viewEvent.date)) {
                    viewState = viewState.copy(
                        startDate = viewEvent.date,
                        showStartDatePicker = false
                    )
                }
            }
            is ComposeEvent.EndDateSelected -> {
                if (viewEvent.date >= (viewState.startDate ?: viewEvent.date)) {
                    viewState = viewState.copy(
                        endDate = viewEvent.date,
                        showEndDatePicker = false
                    )
                }
            }
            ComposeEvent.ShowStartDatePicker -> viewState = viewState.copy(showStartDatePicker = true)
            ComposeEvent.ShowEndDatePicker -> viewState = viewState.copy(showEndDatePicker = true)
            ComposeEvent.HideStartDatePicker -> viewState = viewState.copy(showStartDatePicker = false)
            ComposeEvent.HideEndDatePicker -> viewState = viewState.copy(showEndDatePicker = false)
            ComposeEvent.SaveClicked -> saveHabit()
            ComposeEvent.ClearClicked -> viewState = viewState.copy(habitTitle = "")
            ComposeEvent.CloseClicked -> viewAction = ComposeAction.CloseScreen
        }
    }

    private fun saveHabit() {
        if (viewState.habitTitle.isBlank()) return

        viewState = viewState.copy(isSending = true)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                createHabitUseCase.execute(
                    title = viewState.habitTitle,
                    isGood = viewState.isGoodHabit,
                    type = viewState.habitType,
                    measurement = viewState.measurement,
                    startDate = viewState.startDate?.toString() ?: "",
                    endDate = viewState.endDate?.toString() ?: ""
                )

                withContext(Dispatchers.Main) {
                    viewAction = ComposeAction.Success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(isSending = false)
                    viewAction = ComposeAction.Error
                }
            }
        }
    }
}