package feature.daily.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import di.Inject
import feature.daily.domain.GetHabitsForTodayUseCase
import feature.daily.domain.SwitchHabitUseCase
import feature.daily.ui.models.DailyViewState
import kotlinx.coroutines.launch
import screens.daily.models.DailyAction
import feature.daily.ui.models.DailyEvent
import kotlinx.datetime.*
import screens.daily.views.mapToHabitCardItemModel
import tech.mobiledeveloper.jethabit.app.AppRes
import utils.getTitle

class DailyViewModel : BaseViewModel<DailyViewState, DailyAction, DailyEvent>(
    initialState = DailyViewState()
) {

    private val getHabitsForTodayUseCase = Inject.instance<GetHabitsForTodayUseCase>()
    private val switchHabitUseCase = Inject.instance<SwitchHabitUseCase>()

    private val timeZone = TimeZone.currentSystemDefault()
    private var currentDate = Clock.System.now()

    init {
        fetchHabitFor(date = currentDate.toLocalDateTime(timeZone = timeZone).date)
    }

    override fun obtainEvent(viewEvent: DailyEvent) {
        when (viewEvent) {
            DailyEvent.CloseAction -> TODO()
            DailyEvent.NextDayClicked -> performNextClick()
            is DailyEvent.OnHabitClick -> TODO()
            DailyEvent.PreviousDayClicked -> performPreviousClick()
            DailyEvent.ReloadScreen -> TODO()
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

        viewModelScope.launch {
            val habits = getHabitsForTodayUseCase.execute(date)
                .map { it.mapToHabitCardItemModel() }
            viewState = viewState.copy(habits = habits)
        }
    }

    private fun performNextClick() {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY, timeZone)
        val localDate = currentDate.toLocalDateTime(timeZone).date
        fetchHabitFor(localDate)
    }

    private fun performPreviousClick() {
        currentDate = currentDate.minus(1, DateTimeUnit.DAY, timeZone)
        val localDate = currentDate.toLocalDateTime(timeZone).date
        fetchHabitFor(localDate)
    }
}