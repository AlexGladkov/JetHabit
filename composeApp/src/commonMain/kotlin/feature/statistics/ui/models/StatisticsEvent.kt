package feature.statistics.ui.models

sealed interface StatisticsEvent {
    data object LoadStatistics : StatisticsEvent
    data object NextWeek : StatisticsEvent
    data object PreviousWeek : StatisticsEvent
    data class SwitchTab(val tab: StatisticsTab) : StatisticsEvent
} 