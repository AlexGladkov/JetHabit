package screens.daily

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.daily.DailyRepository
import data.features.habit.HabitRepository
import di.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import screens.daily.models.DailyAction
import screens.daily.models.DailyEvent
import screens.daily.models.DailyViewState
import screens.daily.views.HabitCardItemModel

class DailyViewModel : BaseSharedViewModel<DailyViewState, DailyAction, DailyEvent>(
    initialState = DailyViewState.Loading
) {

    private val habitRepository: HabitRepository = Inject.instance()
    private val dailyRepository: DailyRepository = Inject.instance()

    private var currentDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    override fun obtainEvent(event: DailyEvent) {
        if (event is DailyEvent.CloseAction) {
            viewAction = null
        }

        when (viewState) {
            is DailyViewState.Loading -> reduce(event, viewState as DailyViewState.Loading)
            is DailyViewState.Display -> reduce(event, viewState as DailyViewState.Display)
            is DailyViewState.Error -> reduce(event, viewState as DailyViewState.Error)
            is DailyViewState.NoItems -> reduce(event, viewState as DailyViewState.NoItems)
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.Loading) {
        when (event) {
            DailyEvent.EnterScreen -> fetchHabitForDate()
            else -> {}
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.NoItems) {
        when (event) {
            DailyEvent.ReloadScreen -> fetchHabitForDate(true)
            DailyEvent.EnterScreen -> fetchHabitForDate()
            else -> {}
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.Display) {
        when (event) {
            DailyEvent.EnterScreen -> fetchHabitForDate()
            DailyEvent.NextDayClicked -> performNextClick(currentState.hasNextDay)
            DailyEvent.PreviousDayClicked -> performPreviousClick()
            is DailyEvent.OnHabitClick -> performHabbitClick(
                hasNextDay = currentState.hasNextDay,
                habbitId = event.habitId,
                newValue = event.newValue
            )

            else -> {}
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.Error) {
        when (event) {
            DailyEvent.ReloadScreen -> fetchHabitForDate(needsToRefresh = true)
            else -> {}
        }
    }

    private fun performHabbitClick(hasNextDay: Boolean, habbitId: Long, newValue: Boolean) {
        viewModelScope.launch {
            dailyRepository.addOrUpdate(
                date = convertDateToString(currentDate),
                habitId = habbitId,
                value = newValue
            )

            withContext(Dispatchers.Main) {
                fetchHabitForDate()
            }
        }
    }

    private fun performNextClick(hasNextDay: Boolean) {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val instantCurrentDate = currentDate.toInstant(systemTimeZone)
        val tomorrow = instantCurrentDate.plus(1, DateTimeUnit.DAY, systemTimeZone)
        currentDate = tomorrow.toLocalDateTime(systemTimeZone)
        val duration = Clock.System.now() - instantCurrentDate

        fetchHabitForDate()
    }

    private fun performPreviousClick() {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val now = currentDate.toInstant(systemTimeZone)
        val yesterday = now.minus(1, DateTimeUnit.DAY, systemTimeZone)
        currentDate = yesterday.toLocalDateTime(systemTimeZone)

        fetchHabitForDate()
    }

    private fun getTitleForADay(): String {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val instantCurrentDate = currentDate.toInstant(systemTimeZone)
        val now = Clock.System.now()

        val difference = now - instantCurrentDate

        return when (difference.inWholeDays) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> "${currentDate.dayOfMonth} ${currentDate.month.name.take(3).toLowerCase().capitalize()}"
        }
    }

    private fun fetchHabitForDate(needsToRefresh: Boolean = false) {
        if (needsToRefresh) {
            viewState = DailyViewState.Loading
        }

        viewModelScope.launch {
            try {
                val habits = habitRepository.fetchHabitsList()

                if (habits.isEmpty()) {
                    viewState = DailyViewState.NoItems
                } else {
                    val diaryResponse = dailyRepository.fetchDiary()

                    val dailyActivities = diaryResponse.firstOrNull {
                        val currentDate = currentDate
                        it.date == convertDateToString(currentDate)
                    }

                    val cardItems: List<HabitCardItemModel> = habits.map { habbitEntity ->
                        HabitCardItemModel(
                            habitId = habbitEntity.itemID,
                            title = habbitEntity.title,
                            isChecked = if (dailyActivities != null) {
                                val dailyItem =
                                    dailyActivities.habits.firstOrNull { it.habbitId == habbitEntity.itemID }
                                dailyItem?.value ?: false
                            } else {
                                false
                            }
                        )
                    }


                    viewState = DailyViewState.Display(
                        items = cardItems,
                        hasNextDay = calculateHasNextDay(),
                        title = getTitleForADay()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState = DailyViewState.Error
            }
        }
    }

    private fun convertDateToString(date: LocalDateTime): String {
        return "${date.year}.${date.monthNumber}.${date.dayOfMonth}"
    }

    private fun calculateHasNextDay(): Boolean {
        return convertDateToString(Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault())) != convertDateToString(currentDate)
    }
}