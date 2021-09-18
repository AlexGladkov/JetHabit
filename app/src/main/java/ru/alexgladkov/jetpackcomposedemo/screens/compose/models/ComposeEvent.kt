package ru.alexgladkov.jetpackcomposedemo.screens.compose.models

sealed class ComposeEvent {
    data class TitleChanged(val newValue: String) : ComposeEvent()
    data class CheckboxClicked(val newValue: Boolean) : ComposeEvent()
    object SaveClicked : ComposeEvent()
}
