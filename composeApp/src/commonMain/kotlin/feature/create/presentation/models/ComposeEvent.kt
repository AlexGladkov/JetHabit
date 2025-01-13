package feature.create.presentation.models

import feature.habits.data.HabitType
import feature.habits.data.Measurement
import kotlinx.datetime.LocalDate

sealed interface ComposeEvent {
    data class TitleChanged(val title: String) : ComposeEvent
    data class CheckboxClicked(val isChecked: Boolean) : ComposeEvent
    data class TypeSelected(val type: HabitType) : ComposeEvent
    data class MeasurementSelected(val measurement: Measurement) : ComposeEvent
    data class StartDateSelected(val date: LocalDate) : ComposeEvent
    data class EndDateSelected(val date: LocalDate) : ComposeEvent
    data object ShowStartDatePicker : ComposeEvent
    data object ShowEndDatePicker : ComposeEvent
    data object HideStartDatePicker : ComposeEvent
    data object HideEndDatePicker : ComposeEvent
    data object SaveClicked : ComposeEvent
    data object ClearClicked : ComposeEvent
    data object CloseClicked : ComposeEvent
} 