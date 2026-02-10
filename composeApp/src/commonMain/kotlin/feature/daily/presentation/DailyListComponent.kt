package feature.daily.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.daily.ui.models.DailyEvent
import feature.daily.ui.models.DailyViewState
import feature.projects.domain.GetAllProjectsUseCase
import feature.tracker.domain.UpdateTrackerValueUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import screens.daily.views.mapToHabitCardItemModel
import utils.getTitle

class DailyListComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onHabitSelected: (String) -> Unit,
    private val onComposeClicked: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val getHabitsForTodayUseCase: GetHabitsForTodayUseCase by di.instance()
    private val switchHabitUseCase: SwitchHabitUseCase by di.instance()
    private val updateTrackerValueUseCase: UpdateTrackerValueUseCase by di.instance()
    private val getAllProjectsUseCase: GetAllProjectsUseCase by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val timeZone = TimeZone.currentSystemDefault()
    private var currentDate = Clock.System.now()

    private val _state = MutableValue(DailyViewState())
    val state: Value<DailyViewState> = _state

    init {
        loadProjects()
        fetchHabitFor(date = currentDate.current())
    }

    fun onEvent(viewEvent: DailyEvent) {
        when (viewEvent) {
            DailyEvent.CloseAction -> { /* Not used in component */ }
            DailyEvent.NextDayClicked -> performNextClick()
            is DailyEvent.HabitClicked -> onHabitSelected(viewEvent.habitId)
            DailyEvent.PreviousDayClicked -> performPreviousClick()
            DailyEvent.ReloadScreen -> fetchHabitFor(currentDate.current())
            DailyEvent.ComposeAction -> onComposeClicked()
            is DailyEvent.HabitCheckClicked -> switchCheckForHabit(viewEvent.habitId, viewEvent.newValue)
            is DailyEvent.TrackerValueUpdated -> updateTrackerValue(viewEvent.habitId, viewEvent.value)
            is DailyEvent.ProjectFilterSelected -> {
                _state.value = _state.value.copy(selectedProjectId = viewEvent.projectId)
                fetchHabitFor(currentDate.current())
            }
        }
    }

    private fun loadProjects() {
        scope.launch(Dispatchers.Default) {
            val projects = getAllProjectsUseCase.execute()
            _state.value = _state.value.copy(projects = projects)
        }
    }

    private fun fetchHabitFor(date: LocalDate) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val isToday = date.dayOfYear == today.dayOfYear && date.year == today.year
        val title = date.getTitle()

        _state.value = _state.value.copy(
            currentDay = title,
            hasNextDay = !isToday
        )

        scope.launch(Dispatchers.Default) {
            val habits = getHabitsForTodayUseCase.execute(date, _state.value.selectedProjectId)
                .map { it.mapToHabitCardItemModel() }

            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(habits = habits)
            }
        }
    }

    private fun performNextClick() {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY, timeZone)
        val localDate = currentDate.current()
        fetchHabitFor(localDate)
    }

    private fun performPreviousClick() {
        currentDate = currentDate.minus(1, DateTimeUnit.DAY, timeZone)
        val localDate = currentDate.current()
        fetchHabitFor(localDate)
    }

    private fun switchCheckForHabit(habitId: String, newValue: Boolean) {
        scope.launch(Dispatchers.Default) {
            try {
                // Update database first
                switchHabitUseCase.execute(newValue, habitId, currentDate.current())

                // Then update UI with the latest state from database
                val habits = getHabitsForTodayUseCase.execute(currentDate.current())
                    .map { it.mapToHabitCardItemModel() }

                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(habits = habits)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    fetchHabitFor(currentDate.current())
                }
            }
        }
    }

    private fun updateTrackerValue(habitId: String, value: Double) {
        scope.launch(Dispatchers.Default) {
            updateTrackerValueUseCase.execute(habitId, value, currentDate.current())
            withContext(Dispatchers.Main) {
                fetchHabitFor(currentDate.current())
            }
        }
    }
}

private fun Instant.current(): LocalDate = this.toLocalDateTime(TimeZone.currentSystemDefault()).date
