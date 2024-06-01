package feature.daily.ui.models

sealed class DailyAction {
    class OpenDetail(val itemId: String) : DailyAction()
    data object OpenCompose : DailyAction()
}