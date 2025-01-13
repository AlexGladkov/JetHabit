package screens.compose.models

import feature.habits.data.HabitType
import feature.habits.data.Measurement

data class ComposeViewState(
    val habitTitle: String = "",
    val isGoodHabit: Boolean = true,
    val isSending: Boolean = false,
    val habitType: HabitType = HabitType.REGULAR,
    val measurement: Measurement = Measurement.KILOGRAMS,
    val sendingError: String? = null
)