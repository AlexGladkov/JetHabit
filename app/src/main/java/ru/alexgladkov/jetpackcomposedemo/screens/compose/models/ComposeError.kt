package ru.alexgladkov.jetpackcomposedemo.screens.compose.models

sealed class ComposeError {
    object SendingGeneric : ComposeError()
}