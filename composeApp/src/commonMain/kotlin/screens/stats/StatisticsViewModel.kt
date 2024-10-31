package screens.stats

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import data.features.daily.DailyRepository
import di.Inject
import feature.daily.domain.GetHabitsForTodayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import screens.daily.views.mapToHabitCardItemModel
import screens.stats.models.StatsAction
import screens.stats.models.StatsEvent
import screens.stats.models.StatsViewState
import screens.stats.views.StatisticCellModel

class StatisticsViewModel : BaseViewModel<StatsViewState, StatsAction, StatsEvent>(
    initialState = StatsViewState()
) {

    private val dailyRepository: DailyRepository = Inject.instance()
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
                println("Updated viewState with ${viewState.activeProgress.size} items") // Для отладки
            }
        }
    }

    private suspend fun fetchNonSmokingStatistics(): List<StatisticCellModel> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val habits = getHabitsForTodayUseCase(today)
            .map { it.mapToHabitCardItemModel() }

        val nonSmokingStats = mutableListOf<StatisticCellModel>()

        val habit = habits.find { it.title == "Don't smoke" && it.startDate?.isNotEmpty() == true }
        if (habit?.startDate != null) {
            val startDate = LocalDate.parse(habit.startDate)
            val daysSinceLast = startDate.daysUntil(today)

            val statistic = StatisticCellModel(
                title = "You have not smoked for -> $daysSinceLast days",
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


    private suspend fun fetchActiveMedication(): List<StatisticCellModel> {
//        val medicationList = medicationRepository.fetchCurrentMedications()
//        val diary = dailyRepository.fetchDiary()
//
//        return medicationList.map { medication ->
//            val filtered = diary.filter { entry ->
//                val startDate = getValueOrNull(medication.startDate) ?: return@filter false
//                val endDate = getValueOrNull(medication.endDate) ?: return@filter false
//                val entryDate = DateTime.fromString(entry.date)
//
//                val startComparable = startDate.compareTo(entryDate)
//                val endComparable = endDate.compareTo(entryDate)
//
//                startComparable == 0 && endComparable == 1
//            }
//
//            val startDate = getValueOrNull(medication.startDate)
//            val endDate = getValueOrNull(medication.endDate)
//            val diff = startDate?.let { endDate?.minus(it)?.days } ?: 0.0
//
//            StatisticCellModel(
//                title = medication.title,
//                activeDayList = listOf(true, true, true, false, false),
//                duration = diff.toInt().toString(),
//                fact = filtered.size.toString(),
//                percentage = filtered.size.toFloat() / diff.toFloat(),
//                isPeriodic = false
//            )
//        }

        return emptyList()
    }
}