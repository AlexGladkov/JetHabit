package feature.daily.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.daily.ui.models.DailyViewState
import feature.daily.ui.models.DailyAction
import feature.daily.ui.models.DailyEvent
import feature.tracker.domain.UpdateTrackerValueUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import screens.daily.views.mapToHabitCardItemModel
import utils.getTitle

class DailyViewModel : BaseViewModel<DailyViewState, DailyAction, DailyEvent>(
    initialState = DailyViewState()
) {

    private val getHabitsForTodayUseCase = Inject.instance<GetHabitsForTodayUseCase>()
    private val switchHabitUseCase = Inject.instance<SwitchHabitUseCase>()
    private val updateTrackerValueUseCase = Inject.instance<UpdateTrackerValueUseCase>()

    private val timeZone = TimeZone.currentSystemDefault()
    private var currentDate = Clock.System.now()

    init {
        fetchHabitFor(date = currentDate.current())
    }

    override fun obtainEvent(viewEvent: DailyEvent) {
        when (viewEvent) {
            DailyEvent.CloseAction -> TODO()
            DailyEvent.NextDayClicked -> performNextClick()
            is DailyEvent.HabitClicked -> viewAction = DailyAction.OpenDetail(viewEvent.habitId)
            DailyEvent.PreviousDayClicked -> performPreviousClick()
            DailyEvent.ReloadScreen -> fetchHabitFor(currentDate.current())
            DailyEvent.ComposeAction -> viewAction = DailyAction.OpenCompose
            is DailyEvent.HabitCheckClicked -> switchCheckForHabit(viewEvent.habitId, viewEvent.newValue)
            is DailyEvent.TrackerValueUpdated -> updateTrackerValue(viewEvent.habitId, viewEvent.value)
        }
    }

    private fun fetchHabitFor(date: LocalDate) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val isToday = date.dayOfYear == today.dayOfYear && date.year == today.year
        val title = date.getTitle()
        
        viewState = viewState.copy(
            currentDay = title,
            hasNextDay = !isToday
        )

        viewModelScope.launch(Dispatchers.Default) {
            val habits = getHabitsForTodayUseCase.execute(date)
                .map { it.mapToHabitCardItemModel() }

            withContext(Dispatchers.Main) {
                viewState = viewState.copy(habits = habits)
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
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Update database first
                switchHabitUseCase.execute(newValue, habitId, currentDate.current())
                
                // Then update UI with the latest state from database
                val habits = getHabitsForTodayUseCase.execute(currentDate.current())
                    .map { it.mapToHabitCardItemModel() }
                
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(habits = habits)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    fetchHabitFor(currentDate.current())
                }
            }
        }
    }

    private fun updateTrackerValue(habitId: String, value: Double) {
        viewModelScope.launch(Dispatchers.Default) {
            updateTrackerValueUseCase.execute(habitId, value, currentDate.current())
            withContext(Dispatchers.Main) {
                fetchHabitFor(currentDate.current())
            }
        }
    }
}

private fun Instant.current(): LocalDate = this.toLocalDateTime(TimeZone.currentSystemDefault()).date