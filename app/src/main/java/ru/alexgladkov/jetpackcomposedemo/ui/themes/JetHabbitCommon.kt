package ru.alexgladkov.jetpackcomposedemo.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class JetHabbitColors(
    val primaryText: Color,
    val primaryBackground: Color,
    val secondaryText: Color,
    val secondaryBackground: Color,
    val tintColor: Color
)

object JetHabbitTheme {
    val colors: JetHabbitColors
        @Composable
        get() = LocalJetHabbitColors.current
}

enum class JetHabbitStyle {
    Purple, Orange, Blue
}

val LocalJetHabbitColors = staticCompositionLocalOf<JetHabbitColors> {
    error("No colors provided")
}