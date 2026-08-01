package feature.statistics.ui.models

sealed interface StatisticsEvent {
    data object LoadStatistics : StatisticsEvent

    data class ProjectSelected(
        val projectId: String?,
        val isUncategorizedSelected: Boolean = false
    ) : StatisticsEvent
}
