package screens.stats.models

sealed class StatsEvent {
    object ReloadScreen : StatsEvent()
}
