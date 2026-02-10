package feature.statistics.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.statistics.ui.models.StatisticsEvent
import feature.statistics.ui.models.StatisticsViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import screens.stats.models.HabitStatistics
import screens.stats.models.TrackedDay

class StatisticsComponent(
    componentContext: ComponentContext,
    override val di: DI
) : ComponentContext by componentContext, DIAware {

    private val habitDao: HabitDao by di.instance()
    private val dailyDao: DailyDao by di.instance()
    private val timeZone = TimeZone.currentSystemDefault()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(StatisticsViewState())
    val state: Value<StatisticsViewState> = _state

    init {
        loadHabitsStatistics()
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            StatisticsEvent.LoadStatistics -> loadHabitsStatistics()
        }
    }

    private fun loadHabitsStatistics() {
        scope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(isLoading = true)
            }

            try {
                val habits = habitDao.getAll()
                val now = Clock.System.now()
                val today = now.toLocalDateTime(timeZone).date

                val habitStats = habits.map { habit ->
                    when (habit.type) {
                        HabitType.REGULAR -> {
                            // For regular habits, use the habit's start and end dates
                            val startDate = if (habit.startDate.isBlank()) {
                                today
                            } else {
                                LocalDate.parse(habit.startDate)
                            }

                            val endDate = if (habit.endDate.isBlank()) {
                                today.plus(30, DateTimeUnit.DAY)
                            } else {
                                LocalDate.parse(habit.endDate)
                            }

                            val trackedDays = mutableListOf<TrackedDay>()
                            var currentDate = startDate
                            var trackedCount = 0
                            var totalDays = 0
                            val cleanDaysToCheck = habit.daysToCheck.replace("[", "").replace("]", "")
                            val daysToCheck = cleanDaysToCheck.split(",").map { it.trim().toInt() }

                            while (currentDate <= endDate) {
                                // Only include days that are in daysToCheck
                                if (daysToCheck.contains(currentDate.dayOfWeek.ordinal)) {
                                    totalDays++
                                    val isChecked = dailyDao.isHabitChecked(habit.id, currentDate.toString())
                                    val wasEverChecked = dailyDao.wasDateEverChecked(habit.id, currentDate.toString())
                                    if (isChecked) trackedCount++

                                    trackedDays.add(
                                        TrackedDay(
                                            date = currentDate.toString(),
                                            isChecked = isChecked,
                                            wasEverChecked = wasEverChecked
                                        )
                                    )
                                }
                                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
                            }

                            HabitStatistics(
                                id = habit.id,
                                title = habit.title,
                                trackedDays = trackedDays,
                                completionRate = if (totalDays > 0) trackedCount.toFloat() / totalDays else 0f
                            )
                        }
                        HabitType.TRACKER -> {
                            // For tracker habits, show last 30 days
                            val thirtyDaysAgo = today.minus(30, DateTimeUnit.DAY)
                            val trackedDays = mutableListOf<TrackedDay>()
                            var currentDate = thirtyDaysAgo
                            var trackedCount = 0

                            while (currentDate <= today) {
                                val isChecked = dailyDao.isHabitChecked(habit.id, currentDate.toString())
                                val wasEverChecked = dailyDao.wasDateEverChecked(habit.id, currentDate.toString())
                                if (isChecked) trackedCount++

                                trackedDays.add(
                                    TrackedDay(
                                        date = currentDate.toString(),
                                        isChecked = isChecked,
                                        wasEverChecked = wasEverChecked
                                    )
                                )
                                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
                            }

                            HabitStatistics(
                                id = habit.id,
                                title = habit.title,
                                trackedDays = trackedDays,
                                completionRate = trackedCount.toFloat() / 30f
                            )
                        }
                    }
                }

                // Filter and sort habits:
                // 1. Check if there are any regular habits
                val hasRegularHabits = habits.any { it.type == HabitType.REGULAR }

                // 2. If there are regular habits, show statistics, otherwise show empty screen
                if (hasRegularHabits) {
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            hasData = true,
                            statistics = habitStats,
                            isLoading = false
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            hasData = false,
                            statistics = emptyList(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}
