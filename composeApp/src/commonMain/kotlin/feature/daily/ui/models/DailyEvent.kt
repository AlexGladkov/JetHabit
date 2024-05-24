package feature.daily.ui.models

sealed class DailyEvent {
    data object ReloadScreen : DailyEvent()
    data object PreviousDayClicked : DailyEvent()
    data object NextDayClicked : DailyEvent()
    data object CloseAction : DailyEvent()
    class OnHabitClick(val habitId: Long, val newValue: Boolean) : DailyEvent()
}