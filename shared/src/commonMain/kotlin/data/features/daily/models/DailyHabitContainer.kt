package data.features.daily.models

import kotlinx.serialization.Serializable

@Serializable
data class DailyHabitContainer(
    val habbitId: Long,
    val value: Boolean
)
