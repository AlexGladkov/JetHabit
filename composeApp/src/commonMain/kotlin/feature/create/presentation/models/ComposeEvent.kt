package feature.create.presentation.models

import feature.habits.data.HabitType
import feature.habits.data.Measurement

sealed class ComposeEvent {
    data class TitleChanged(val value: String) : ComposeEvent()
    data class CheckboxClicked(val value: Boolean) : ComposeEvent()
    data class TypeSelected(val type: HabitType) : ComposeEvent()
    data class MeasurementSelected(val measurement: Measurement) : ComposeEvent()
    data object SaveClicked : ComposeEvent()
    data object ClearClicked : ComposeEvent()
    data object CloseClicked : ComposeEvent()
} 