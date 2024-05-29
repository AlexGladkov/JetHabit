package feature.settings.presentation.models

data class SettingsViewState(
    val username: String = "",
    val avatar: String = "",
    val healthPercentage: Int = 100
)