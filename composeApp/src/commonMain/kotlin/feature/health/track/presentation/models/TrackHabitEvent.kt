package feature.health.track.presentation.models

sealed interface TrackHabitEvent {
    data class NewValueChanged(val value: String) : TrackHabitEvent
    data object SaveClicked : TrackHabitEvent
    data object CloseClicked : TrackHabitEvent
} 