package ru.alexgladkov.jetpackcomposedemo.screens.compose.models

sealed class ComposeViewState {

    data class ViewStateInitial(
        val habbitTitle: String = "",
        val isGoodHabbit: Boolean = true,
        val isSending: Boolean = false,
        val sendingError: ComposeError? = null
    ) : ComposeViewState()

    object ViewStateSuccess : ComposeViewState()
}