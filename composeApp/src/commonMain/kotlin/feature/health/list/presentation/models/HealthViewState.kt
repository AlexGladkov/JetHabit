package feature.health.list.presentation.models

data class HealthViewState(
    val habits: List<TrackerHabitItem> = emptyList(),
    val isLoading: Boolean = false
) 