package feature.health.presentation.models

data class HealthViewState(
    val trackerHistory: List<TrackerHistoryItem> = emptyList(),
    val isLoading: Boolean = false
) 