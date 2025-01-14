package feature.profile.edit.ui.models

sealed class EditProfileAction {
    data object NavigateBack : EditProfileAction()
} 