package screens.stats

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import screens.stats.models.HabitStatistics
import screens.stats.models.StatsAction
import screens.stats.models.StatsEvent
import screens.stats.models.StatsViewState
import screens.stats.models.TrackedDay

class StatisticsViewModel : BaseViewModel<StatsViewState, StatsAction, StatsEvent>(
    initialState = StatsViewState()
) {
    private val habitDao: HabitDao = Inject.instance()
    private val dailyDao: DailyDao = Inject.instance()
    private val timeZone = TimeZone.currentSystemDefault()

    init {
        loadHabitsStatistics()
    }

    override fun obtainEvent(viewEvent: StatsEvent) {
        when (viewEvent) {
            StatsEvent.ReloadScreen -> loadHabitsStatistics()
        }
    }

    private fun loadHabitsStatistics() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(isLoading = true)
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
                                    if (isChecked) trackedCount++
                                    
                                    trackedDays.add(
                                        TrackedDay(
                                            date = currentDate.toString(),
                                            isChecked = isChecked
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
                                if (isChecked) trackedCount++
                                
                                trackedDays.add(
                                    TrackedDay(
                                        date = currentDate.toString(),
                                        isChecked = isChecked
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

                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        habits = habitStats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(isLoading = false)
                }
            }
        }
    }
}