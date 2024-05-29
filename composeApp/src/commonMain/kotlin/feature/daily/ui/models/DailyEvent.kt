package feature.daily.ui.models

sealed class DailyEvent {
    data object ReloadScreen : DailyEvent()
    data object PreviousDayClicked : DailyEvent()
    data object NextDayClicked : DailyEvent()
    data object CloseAction : DailyEvent()
    data object ComposeAction : DailyEvent()
    class HabitClicked(val habitId: Long) : DailyEvent()
    class HabitCheckClicked(val habitId: Long, val newValue: Boolean) : DailyEvent()
}