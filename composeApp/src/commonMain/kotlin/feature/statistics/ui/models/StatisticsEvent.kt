package feature.statistics.ui.models

sealed interface StatisticsEvent {
    data object LoadStatistics : StatisticsEvent
} 