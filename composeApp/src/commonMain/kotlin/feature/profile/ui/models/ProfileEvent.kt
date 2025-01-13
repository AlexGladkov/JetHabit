package feature.profile.ui.models

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object OpenSettings : ProfileEvent
} 