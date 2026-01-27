package feature.profile.start.ui.models

import di.Platform

sealed interface ProfileEvent {
    data class PickImageFromLibrary(val platform: Platform) : ProfileEvent
    data class TakePhoto(val platform: Platform) : ProfileEvent
    data object EditProfileClicked : ProfileEvent
    data object OpenSettings : ProfileEvent
    data object OpenProjects : ProfileEvent
    data object LoadProfile : ProfileEvent
} 