package feature.statistics.ui.models

sealed interface StatisticsEvent {
    data object LoadStatistics : StatisticsEvent

    data class ProjectSelected(
        val projectId: String?,
        val isUncategorizedSelected: Boolean = false
    ) : StatisticsEvent

    // E7: habit heatmap events
    data class DayCellClicked(val cellIndex: Int) : StatisticsEvent
    data object DayDetailsDismissed : StatisticsEvent
}
