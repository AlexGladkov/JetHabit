package domain

import ui.themes.JetHabitCorners
import ui.themes.JetHabitSize
import ui.themes.JetHabitStyle

data class SettingsBundle(
    val isDarkMode: Boolean,
    val textSize: JetHabitSize,
    val paddingSize: JetHabitSize,
    val cornerStyle: JetHabitCorners,
    val style: JetHabitStyle
)