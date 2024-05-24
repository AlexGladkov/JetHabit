package feature.daily.ui.models

sealed class DailyAction {
    class OpenDetail(val itemId: Long) : DailyAction()
    data object OpenCompose : DailyAction()
}