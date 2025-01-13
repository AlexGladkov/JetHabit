package feature.health.list.presentation.models

import feature.habits.data.Measurement

data class TrackerHabitItem(
    val id: String,
    val title: String,
    val measurement: Measurement,
    val lastValue: Double?,
    val values: List<Double>,
    val dates: List<String>
) 