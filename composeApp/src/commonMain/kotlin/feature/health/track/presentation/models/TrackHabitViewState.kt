package feature.health.track.presentation.models

import feature.habits.data.Measurement

data class TrackHabitViewState(
    val title: String = "",
    val measurement: Measurement = Measurement.KILOGRAMS,
    val newValue: Double? = null,
    val history: List<TrackerHistoryItem> = emptyList(),
    val isLoading: Boolean = false
)

data class TrackerHistoryItem(
    val title: String,
    val value: Double,
    val date: String,
    val measurement: Measurement
) 