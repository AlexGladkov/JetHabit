package feature.profile.ui.models

sealed interface ProfileAction {
    data object NavigateToSettings : ProfileAction
} 