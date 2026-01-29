package feature.statistics.ui.models

import androidx.compose.runtime.Stable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import screens.stats.models.HabitStatistics

enum class StatisticsTab {
    WEEKLY,
    ALL_TIME
}

data class DayStatus(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val isChecked: Boolean,
    val isApplicable: Boolean // whether habit is scheduled for this day
)

data class WeeklyHabitStat(
    val habitId: String,
    val habitTitle: String,
    val dailyStatus: List<DayStatus> // 7 entries, Mon-Sun
)

data class WeeklyData(
    val weekStart: LocalDate, // Monday
    val weekEnd: LocalDate,   // Sunday
    val completedCount: Int,
    val totalCount: Int,
    val completionRate: Float,
    val dailyCompletionCounts: List<Int>, // 7 entries (completed habits per day)
    val dailyTotalCounts: List<Int>,      // 7 entries (total applicable habits per day)
    val habitStats: List<WeeklyHabitStat>
)

@Stable
data class StatisticsViewState(
    val isLoading: Boolean = false,
    val hasData: Boolean = false,
    val statistics: List<HabitStatistics> = emptyList(),
    val activeTab: StatisticsTab = StatisticsTab.WEEKLY,
    val weeklyData: WeeklyData? = null
) 