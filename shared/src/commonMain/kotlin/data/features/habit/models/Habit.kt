package data.features.habit.models

import kotlinx.datetime.Instant

data class Habit(
    val itemId: Long,
    val title: String,
    val startDate: Instant?,
    val endDate: Instant?,
    val isGood: Boolean
)
