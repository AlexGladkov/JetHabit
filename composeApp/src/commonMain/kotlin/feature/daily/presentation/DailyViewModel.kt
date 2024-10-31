package feature.daily.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.daily.ui.models.DailyViewState
import kotlinx.coroutines.launch
import feature.daily.ui.models.DailyAction
import feature.daily.ui.models.DailyEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import screens.daily.views.mapToHabitCardItemModel
import utils.getTitle
import utils.notifications.HabbitNotificationManager
import utils.tenDaysPassed

class DailyViewModel : BaseViewModel<DailyViewState, DailyAction, DailyEvent>(
    initialState = DailyViewState()
) {

    private val getHabitsForTodayUseCase = Inject.instance<GetHabitsForTodayUseCase>()
    private val switchHabitUseCase = Inject.instance<SwitchHabitUseCase>()

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
            is DailyEvent.HabitCheckClicked -> switchCheckForHabit(
                viewEvent.habitId,
                viewEvent.newValue
            )
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
            val habits = getHabitsForTodayUseCase(date)
                .map { it.mapToHabitCardItemModel() }

            val habit = habits.find { it.title == "Don't smoke" && !it.startDate.isNullOrEmpty() }
                ?: return@launch

            val startDate = LocalDate.parse(habit.startDate.orEmpty())
            if (tenDaysPassed(startDate)) {
                HabbitNotificationManager.create().sendNotification(
                    "Well done!",
                    "You have not smoked more than 10 days!!!")
            }

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
            switchHabitUseCase.execute(newValue, habitId, currentDate.current())
            withContext(Dispatchers.Main) {
                fetchHabitFor(currentDate.current())
            }
        }
    }
}

private fun Instant.current(): LocalDate =
    this.toLocalDateTime(TimeZone.currentSystemDefault()).date