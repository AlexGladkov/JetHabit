package screens.stats

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.habit.HabitRepository
import di.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import screens.stats.models.StatsAction
import screens.stats.models.StatsEvent
import screens.stats.models.StatsViewState
import screens.stats.views.StatisticCellModel

class StatisticsViewModel : BaseSharedViewModel<StatsViewState, StatsAction, StatsEvent>(
    initialState = StatsViewState()
) {

    private val habitRepository: HabitRepository = Inject.instance()
    private var currentDate = Clock.System.now()

    init {
        loadActivities()
    }

    override fun obtainEvent(viewEvent: StatsEvent) {

    }

    private fun loadActivities() {
        viewModelScope.launch {
            val habitsList = habitRepository.fetchHabitsList()
            val activeProgress = habitsList.map {
                StatisticCellModel(
                    title = it.title,
                    activeDayList = listOf(true, true, true, false, false),
                    isPeriodic = !(it.startDate == null || it.endDate == null)
                )
            }

            viewState = viewState.copy(activeProgress = activeProgress)
        }
    }
}