package feature.statistics.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.statistics.ui.models.StatisticsAction
import feature.statistics.ui.models.StatisticsEvent
import feature.statistics.ui.models.StatisticsViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import screens.stats.models.HabitStatistics
import screens.stats.models.TrackedDay

class StatisticsViewModel : BaseViewModel<StatisticsViewState, StatisticsAction, StatisticsEvent>(
    initialState = StatisticsViewState()
) {
    private val habitDao = Inject.instance<HabitDao>()
    private val dailyDao = Inject.instance<DailyDao>()
    private val timeZone = TimeZone.currentSystemDefault()

    init {
        loadHabitsStatistics()
    }

    override fun obtainEvent(event: StatisticsEvent) {
        when (event) {
            StatisticsEvent.LoadStatistics -> loadHabitsStatistics()
        }
    }

    /**
     * Calculate current streak counting backwards from today.
     * Streak is strict: if today is not checked, streak is 0.
     */
    private fun calculateCurrentStreak(trackedDays: List<TrackedDay>, today: LocalDate): Int {
        var streak = 0
        var currentDate = today

        while (true) {
            val dateStr = currentDate.toString()
            val trackedDay = trackedDays.find { it.date == dateStr }

            if (trackedDay != null && trackedDay.isChecked) {
                streak++
                currentDate = currentDate.minus(1, DateTimeUnit.DAY)
            } else {
                break
            }
        }

        return streak
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
                                completionRate = if (totalDays > 0) trackedCount.toFloat() / totalDays else 0f,
                                currentStreak = calculateCurrentStreak(trackedDays, today)
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
                                completionRate = trackedCount.toFloat() / 30f,
                                currentStreak = calculateCurrentStreak(trackedDays, today)
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
                        viewState = viewState.copy(
                            hasData = true,
                            statistics = habitStats,
                            isLoading = false
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        viewState = viewState.copy(
                            hasData = false,
                            statistics = emptyList(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(isLoading = false)
                }
            }
        }
    }
} 