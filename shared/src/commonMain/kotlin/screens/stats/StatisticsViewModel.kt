package screens.stats

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.daily.DailyRepository
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
import utils.parseToDate

class StatisticsViewModel : BaseSharedViewModel<StatsViewState, StatsAction, StatsEvent>(
    initialState = StatsViewState()
) {

    private val habitRepository: HabitRepository = Inject.instance()
    private val dailyRepository: DailyRepository = Inject.instance()

    init {
        loadActivities()
    }

    override fun obtainEvent(viewEvent: StatsEvent) {

    }

    private fun loadActivities() {
        viewModelScope.launch {
            val habitsList = habitRepository.fetchHabitsList()
            val diary = dailyRepository.fetchDiary()

            val activeProgress = habitsList.map { habit ->
                val filtered = diary.filter { entry ->
                    val startDuration = habit.startDate?.minus(entry.date.parseToDate())?.inWholeDays ?: 0
                    val endDuration = habit.endDate?.minus(entry.date.parseToDate())?.inWholeDays ?: 0

                    if (startDuration > 0) return@filter false
                    if (endDuration < 0) return@filter false

                    entry.habits.firstOrNull { it.habbitId == habit.itemId } != null
                }

                val startDate = habit.startDate
                val endDate = habit.endDate

                val duration =
                    if (startDate != null && endDate != null)
                        endDate.minus(startDate).inWholeDays.toString()
                    else ""

                val fact = filtered.size
                val percentage = if (duration.isNotBlank()) fact.toFloat() / duration.toFloat() else
                    0.0f

                StatisticCellModel(
                    title = habit.title,
                    activeDayList = listOf(true, true, true, false, false),
                    duration = duration,
                    fact = fact.toString(),
                    percentage = percentage,
                    isPeriodic = !(habit.startDate == null || habit.endDate == null)
                )
            }

            viewState = viewState.copy(activeProgress = activeProgress)
        }
    }
}