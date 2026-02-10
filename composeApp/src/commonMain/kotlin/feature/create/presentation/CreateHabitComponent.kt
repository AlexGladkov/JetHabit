package feature.create.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.create.presentation.models.ComposeEvent
import feature.create.presentation.models.ComposeViewState
import feature.habits.domain.CreateHabitUseCase
import feature.projects.domain.GetAllProjectsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class CreateHabitComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val habitType: String? = null,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val createHabitUseCase: CreateHabitUseCase by di.instance()
    private val getAllProjectsUseCase: GetAllProjectsUseCase by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(
        ComposeViewState(
            startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(30, DateTimeUnit.DAY)
        )
    )
    val state: Value<ComposeViewState> = _state

    init {
        loadProjects()
    }

    fun onEvent(viewEvent: ComposeEvent) {
        when (viewEvent) {
            is ComposeEvent.TitleChanged -> _state.value = _state.value.copy(habitTitle = viewEvent.title)
            is ComposeEvent.CheckboxClicked -> _state.value = _state.value.copy(isGoodHabit = viewEvent.isChecked)
            is ComposeEvent.TypeSelected -> _state.value = _state.value.copy(habitType = viewEvent.type)
            is ComposeEvent.MeasurementSelected -> _state.value = _state.value.copy(measurement = viewEvent.measurement)
            is ComposeEvent.ProjectSelected -> _state.value = _state.value.copy(selectedProjectId = viewEvent.projectId)
            is ComposeEvent.StartDateSelected -> {
                if (viewEvent.date <= (_state.value.endDate ?: viewEvent.date)) {
                    _state.value = _state.value.copy(
                        startDate = viewEvent.date,
                        showStartDatePicker = false
                    )
                }
            }
            is ComposeEvent.EndDateSelected -> {
                if (viewEvent.date >= (_state.value.startDate ?: viewEvent.date)) {
                    _state.value = _state.value.copy(
                        endDate = viewEvent.date,
                        showEndDatePicker = false
                    )
                }
            }
            ComposeEvent.ShowStartDatePicker -> _state.value = _state.value.copy(showStartDatePicker = true)
            ComposeEvent.ShowEndDatePicker -> _state.value = _state.value.copy(showEndDatePicker = true)
            ComposeEvent.HideStartDatePicker -> _state.value = _state.value.copy(showStartDatePicker = false)
            ComposeEvent.HideEndDatePicker -> _state.value = _state.value.copy(showEndDatePicker = false)
            ComposeEvent.SaveClicked -> saveHabit()
            ComposeEvent.ClearClicked -> _state.value = _state.value.copy(habitTitle = "")
            ComposeEvent.CloseClicked -> onNavigateBack()
        }
    }

    private fun loadProjects() {
        scope.launch(Dispatchers.Default) {
            val projects = getAllProjectsUseCase.execute()
            _state.value = _state.value.copy(projects = projects)
        }
    }

    private fun saveHabit() {
        if (_state.value.habitTitle.isBlank()) return

        _state.value = _state.value.copy(isSending = true)
        scope.launch(Dispatchers.Default) {
            try {
                createHabitUseCase.execute(
                    title = _state.value.habitTitle,
                    isGood = _state.value.isGoodHabit,
                    type = _state.value.habitType,
                    measurement = _state.value.measurement,
                    startDate = _state.value.startDate?.toString() ?: "",
                    endDate = _state.value.endDate?.toString() ?: "",
                    projectId = _state.value.selectedProjectId
                )

                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isSending = false)
                    onNavigateBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isSending = false)
                }
            }
        }
    }
}
