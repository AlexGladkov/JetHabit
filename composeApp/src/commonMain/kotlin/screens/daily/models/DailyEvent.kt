package screens.daily.models

sealed class DailyEvent {
    object EnterScreen : DailyEvent()
    object ReloadScreen : DailyEvent()
    object PreviousDayClicked : DailyEvent()
    object NextDayClicked : DailyEvent()
    object CloseAction : DailyEvent()
    data class OnHabitClick(val habitId: Long, val newValue: Boolean) : DailyEvent()
}