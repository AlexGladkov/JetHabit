package screens.stats.models

data class StatsViewState(
    val habits: List<HabitStatistics> = emptyList(),
    val isLoading: Boolean = false
)

data class HabitStatistics(
    val id: String,
    val title: String,
    val trackedDays: List<TrackedDay>,
    val completionRate: Float // Percentage of days tracked
)

data class TrackedDay(
    val date: String,
    val isChecked: Boolean
)