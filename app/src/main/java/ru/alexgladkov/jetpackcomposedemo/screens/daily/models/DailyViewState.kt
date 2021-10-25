package ru.alexgladkov.jetpackcomposedemo.screens.daily.models

import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabitCardItemModel

sealed class DailyViewState {
    object Loading : DailyViewState()
    object Error : DailyViewState()
    data class Display(
        val items: List<HabitCardItemModel>,
        val title: String,
        val hasNextDay: Boolean
    ) : DailyViewState()
    object NoItems: DailyViewState()
}