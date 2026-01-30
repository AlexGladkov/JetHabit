package feature.profile.start.ui.models

sealed class ProfileAction {
    data object NavigateToEdit : ProfileAction()
    data object NavigateToSettings : ProfileAction()
    data object NavigateToProjects : ProfileAction()
    data object LaunchVkLogin : ProfileAction()
    data class ShowError(val message: String) : ProfileAction()
} 