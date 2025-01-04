package feature.health.presentation.models

import feature.habits.data.Measurement

data class TrackerHistoryItem(
    val title: String,
    val value: Double,
    val date: String,
    val measurement: Measurement
) 