package screens.stats

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.daily.domain.GetHabitsForTodayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import screens.daily.views.daysSinceHabitStarted
import screens.daily.views.mapToHabitCardItemModel
import screens.stats.models.StatsAction
import screens.stats.models.StatsEvent
import screens.stats.models.StatsViewState
import screens.stats.views.StatisticCellModel
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.no_smoke_ten_days

class StatisticsViewModel : BaseViewModel<StatsViewState, StatsAction, StatsEvent>(
    initialState = StatsViewState()
) {
    private val getHabitsForTodayUseCase = Inject.instance<GetHabitsForTodayUseCase>()

    init {
        loadActivities()
    }

    override fun obtainEvent(viewEvent: StatsEvent) {
        when (viewEvent) {
            StatsEvent.ReloadScreen -> loadActivities()
        }
    }

    private fun loadActivities() {
        viewModelScope.launch(Dispatchers.IO) {
            val nonSmokingStatistics = fetchNonSmokingStatistics()
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(activeProgress = nonSmokingStatistics)
            }
        }
    }

    private suspend fun fetchNonSmokingStatistics(): List<StatisticCellModel> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val habits = getHabitsForTodayUseCase.execute(today)
            .map { it.mapToHabitCardItemModel() }

        val nonSmokingStats = mutableListOf<StatisticCellModel>()
        val daysSinceLast = habits.daysSinceHabitStarted(today)

        if (daysSinceLast != null) {
            val statistic = StatisticCellModel(
                title = getString(Res.string.no_smoke_ten_days),
                activeDayList = emptyList(),
                duration = "$daysSinceLast days",
                fact = daysSinceLast.toString(),
                percentage = 1f,
                isPeriodic = false
            )

            nonSmokingStats.add(statistic)
        }
        return nonSmokingStats
    }
}