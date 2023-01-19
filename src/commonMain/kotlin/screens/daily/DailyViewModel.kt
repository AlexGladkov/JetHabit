package screens.daily

import com.adeo.kviewmodel.BaseSharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.alexgladkov.jetpackcomposedemo.screens.daily.models.DailyEvent
import screens.daily.models.DailyViewState
import screens.daily.views.HabitCardItemModel

class DailyViewModel : BaseSharedViewModel<DailyViewState, Unit, DailyEvent>(
    initialState = DailyViewState.Loading
) {

//    private val habitRepository: HabitRepository,
//    private val dailyRepository: DailyRepository

//    private var currentDate: Date = Calendar.getInstance().time

    override fun obtainEvent(event: DailyEvent) {
        when (viewState) {
            is DailyViewState.Loading -> reduce(event, viewState as DailyViewState.Loading)
            is DailyViewState.Display -> reduce(event, viewState as DailyViewState.Display)
            is DailyViewState.Error -> reduce(event, viewState as DailyViewState.Error)
            is DailyViewState.NoItems -> reduce(event, viewState as DailyViewState.NoItems)
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.Loading) {
        when (event) {
            DailyEvent.EnterScreen -> fetchHabbitForDate()
            else -> {}
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.NoItems) {
        when (event) {
            DailyEvent.ReloadScreen -> fetchHabbitForDate(true)
            DailyEvent.EnterScreen -> fetchHabbitForDate()
            else -> {}
        }
    }

    private fun reduce(event: DailyEvent, currentState: DailyViewState.Display) {
        when (event) {
            DailyEvent.EnterScreen -> fetchHabbitForDate()
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
            DailyEvent.ReloadScreen -> fetchHabbitForDate(needsToRefresh = true)
            else -> {}
        }
    }

    private fun performHabbitClick(hasNextDay: Boolean, habbitId: Long, newValue: Boolean) {
        viewModelScope.launch {
//            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
//            dailyRepository.addOrUpdate(
//                date = dateFormat.format(currentDate),
//                habitId = habbitId,
//                value = newValue
//            )
//
//            withContext(Dispatchers.Main) {
//                fetchHabbitForDate(setHasNextDay = hasNextDay)
//            }
        }
    }

    private fun performNextClick(hasNextDay: Boolean) {
//        val calendar = Calendar.getInstance()
//        calendar.time = currentDate
//        calendar.add(Calendar.DAY_OF_MONTH, 1)
//        currentDate = calendar.time
//
//        val todayCalendar = Calendar.getInstance()
//        val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)
//        val todayMonth = todayCalendar.get(Calendar.MONTH)
//        val todayYear = todayCalendar.get(Calendar.YEAR)
//
//        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
//        val currentMonth = calendar.get(Calendar.MONTH)
//        val currentYear = calendar.get(Calendar.YEAR)
//
//        fetchHabbitForDate(
//            setHasNextDay = if (
//                currentDay == todayDay
//                && currentMonth == todayMonth
//                && currentYear == todayYear
//            ) false else hasNextDay
//        )
    }

    private fun performPreviousClick() {
//        val calendar = Calendar.getInstance()
//        calendar.time = currentDate
//        calendar.add(Calendar.DAY_OF_MONTH, -1)
//        currentDate = calendar.time
//
//        fetchHabbitForDate(setHasNextDay = true)
    }

    private fun getTitleForADay(): String {
//        val calendar = Calendar.getInstance()
//        calendar.time = currentDate
//
//        val difference = Calendar.getInstance().timeInMillis - calendar.timeInMillis
//        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
//
//        return when (TimeUnit.MILLISECONDS.toDays(difference)) {
//            0L -> "Today"
//            1L -> "Yesterday"
//            else -> dateFormat.format(currentDate)
//        }
        return ""
    }

    private fun fetchHabbitForDate(needsToRefresh: Boolean = false, setHasNextDay: Boolean = false) {
//        if (needsToRefresh) {
//            _dailyViewState.postValue(DailyViewState.Loading)
//        }
//
//        viewModelScope.launch {
//            try {
//                val habbits = habitRepository.fetchHabitsList()
//
//                if (habbits.isEmpty()) {
//                    _dailyViewState.postValue(DailyViewState.NoItems)
//                } else {
//                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
//                    val diaryResponse = dailyRepository.fetchDiary()
//                    Log.e("TAG", "response -> $diaryResponse")
//                    val dailyActivities = diaryResponse
//                        .filter { it.date == dateFormat.format(currentDate) }.firstOrNull()
//
//                    val cardItems: List<HabitCardItemModel> = habbits.map { habbitEntity ->
//                        HabitCardItemModel(
//                            habitId = habbitEntity.itemId,
//                            title = habbitEntity.title,
//                            isChecked = if (dailyActivities != null) {
//                                val dailyItem = dailyActivities.habits.firstOrNull { it.habbitId == habbitEntity.itemId }
//                                dailyItem?.value ?: false
//                            } else {
//                                false
//                            }
//                        )
//                    }
//
//
//                    _dailyViewState.postValue(
//                        DailyViewState.Display(
//                            items = cardItems,
//                            hasNextDay = setHasNextDay,
//                            title = getTitleForADay()
//                        )
//                    )
//                }
//            } catch (e: Exception) {
//                _dailyViewState.postValue(DailyViewState.Error)
//            }
//        }
    }
}