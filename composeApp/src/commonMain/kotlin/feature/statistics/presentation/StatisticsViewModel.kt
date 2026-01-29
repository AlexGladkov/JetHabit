package feature.statistics.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.daily.data.DailyDao
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.statistics.ui.models.*
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
    private lateinit var currentWeekStart: LocalDate

    init {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(timeZone).date
        currentWeekStart = getWeekStart(today)
        loadHabitsStatistics()
        loadWeeklyStatistics(currentWeekStart)
    }

    override fun obtainEvent(event: StatisticsEvent) {
        when (event) {
            StatisticsEvent.LoadStatistics -> loadHabitsStatistics()
            StatisticsEvent.NextWeek -> navigateToNextWeek()
            StatisticsEvent.PreviousWeek -> navigateToPreviousWeek()
            is StatisticsEvent.SwitchTab -> switchTab(event.tab)
        }
    }

    private fun navigateToNextWeek() {
        currentWeekStart = currentWeekStart.plus(7, DateTimeUnit.DAY)
        loadWeeklyStatistics(currentWeekStart)
    }

    private fun navigateToPreviousWeek() {
        currentWeekStart = currentWeekStart.minus(7, DateTimeUnit.DAY)
        loadWeeklyStatistics(currentWeekStart)
    }

    private fun switchTab(tab: StatisticsTab) {
        viewState = viewState.copy(activeTab = tab)
    }

    private fun getWeekStart(date: LocalDate): LocalDate {
        // Get Monday of the current week (Monday = DayOfWeek.MONDAY which has ordinal 0)
        val dayOfWeek = date.dayOfWeek.ordinal // Monday = 0, Sunday = 6
        return date.minus(dayOfWeek, DateTimeUnit.DAY)
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

    private fun loadWeeklyStatistics(weekStart: LocalDate) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val habits = habitDao.getAll()
                val weekEnd = weekStart.plus(6, DateTimeUnit.DAY) // Sunday

                // Filter only regular habits for weekly statistics
                val regularHabits = habits.filter { it.type == HabitType.REGULAR }

                if (regularHabits.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        viewState = viewState.copy(weeklyData = null)
                    }
                    return@launch
                }

                val dailyCompletionCounts = MutableList(7) { 0 }
                val dailyTotalCounts = MutableList(7) { 0 }
                var totalCompleted = 0
                var totalApplicable = 0

                val habitStats = regularHabits.map { habit ->
                    val cleanDaysToCheck = habit.daysToCheck.replace("[", "").replace("]", "")
                    val daysToCheck = cleanDaysToCheck.split(",").map { it.trim().toInt() }

                    val startDate = if (habit.startDate.isBlank()) {
                        weekStart
                    } else {
                        LocalDate.parse(habit.startDate)
                    }

                    val endDate = if (habit.endDate.isBlank()) {
                        weekEnd.plus(1, DateTimeUnit.YEAR) // Far future
                    } else {
                        LocalDate.parse(habit.endDate)
                    }

                    val dailyStatus = (0..6).map { dayOffset ->
                        val date = weekStart.plus(dayOffset, DateTimeUnit.DAY)
                        val dayOfWeek = date.dayOfWeek

                        // Check if this day is within the habit's date range and is scheduled
                        val isApplicable = date >= startDate &&
                                          date <= endDate &&
                                          daysToCheck.contains(dayOfWeek.ordinal)

                        val isChecked = if (isApplicable) {
                            dailyDao.isHabitChecked(habit.id, date.toString())
                        } else {
                            false
                        }

                        if (isApplicable) {
                            dailyTotalCounts[dayOffset]++
                            totalApplicable++
                            if (isChecked) {
                                dailyCompletionCounts[dayOffset]++
                                totalCompleted++
                            }
                        }

                        DayStatus(
                            date = date,
                            dayOfWeek = dayOfWeek,
                            isChecked = isChecked,
                            isApplicable = isApplicable
                        )
                    }

                    WeeklyHabitStat(
                        habitId = habit.id,
                        habitTitle = habit.title,
                        dailyStatus = dailyStatus
                    )
                }

                val completionRate = if (totalApplicable > 0) {
                    totalCompleted.toFloat() / totalApplicable
                } else {
                    0f
                }

                val weeklyData = WeeklyData(
                    weekStart = weekStart,
                    weekEnd = weekEnd,
                    completedCount = totalCompleted,
                    totalCount = totalApplicable,
                    completionRate = completionRate,
                    dailyCompletionCounts = dailyCompletionCounts,
                    dailyTotalCounts = dailyTotalCounts,
                    habitStats = habitStats
                )

                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(weeklyData = weeklyData)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(weeklyData = null)
                }
            }
        }
    }
} 