package feature.profile.start.ui.models

sealed class ProfileAction {
    data object NavigateToEdit : ProfileAction()
    data object NavigateToSettings : ProfileAction()
} 