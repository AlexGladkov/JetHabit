package feature.health.track.presentation.models

sealed interface TrackHabitEvent {
    data class NewValueChanged(val value: String) : TrackHabitEvent
    data class DateSelected(val date: String) : TrackHabitEvent
    data object SaveClicked : TrackHabitEvent
    data object CloseClicked : TrackHabitEvent
}

sealed interface TrackHabitAction {
    data object NavigateBack : TrackHabitAction
} 