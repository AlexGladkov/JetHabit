package feature.profile.edit.ui.models

sealed interface EditProfileEvent {
    data class NameChanged(val name: String) : EditProfileEvent
    data class EmailChanged(val email: String) : EditProfileEvent
    data object SaveClicked : EditProfileEvent
    data object BackClicked : EditProfileEvent
    data object LoadProfile : EditProfileEvent
} 