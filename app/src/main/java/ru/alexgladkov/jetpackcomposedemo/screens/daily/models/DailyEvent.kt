package ru.alexgladkov.jetpackcomposedemo.screens.daily.models

sealed class DailyEvent {
    object EnterScreen : DailyEvent()
    object ReloadScreen : DailyEvent()
    object PreviousDayClicked : DailyEvent()
    object NextDayClicked : DailyEvent()
    data class OnHabbitClick(val habbitId: Long, val newValue: Boolean) : DailyEvent()
}