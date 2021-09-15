package ru.alexgladkov.jetpackcomposedemo.screens.daily.models

import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitEntity
import java.util.Calendar
import java.util.Date

sealed class DailyViewState {
    object Loading : DailyViewState()
    object Error : DailyViewState()
    data class Display(
        val items: List<HabbitEntity>,
        val title: String,
        val hasNextDay: Boolean
    ) : DailyViewState()
    object NoItems: DailyViewState()
}