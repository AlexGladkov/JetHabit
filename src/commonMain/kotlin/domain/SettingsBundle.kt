package domain

import androidx.compose.runtime.staticCompositionLocalOf
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle

data class SettingsBundle(
    val isDarkMode: Boolean,
    val textSize: JetHabitSize,
    val paddingSize: JetHabitSize,
    val cornerStyle: JetHabitCorners,
    val style: JetHabitStyle
)