package ru.alexgladkov.jetpackcomposedemo.ui.themes

import androidx.compose.ui.graphics.Color

val baseLightPalette = JetHabbitColors(
    primaryBackground = Color(0xFFFFFFFF),
    primaryText = Color(0xFF3D454C),
    secondaryBackground = Color(0xFFF3F4F5),
    secondaryText = Color(0xCC7A8A99),
    tintColor = Color.Magenta
)

val baseDarkPalette = JetHabbitColors(
    primaryBackground = Color(0xFF23282D),
    primaryText = Color(0xFFF2F4F5),
    secondaryBackground = Color(0xFF191E23),
    secondaryText = Color(0xCC7A8A99),
    tintColor = Color.Magenta
)

val purpleLightPalette = baseLightPalette.copy(
    tintColor = Color(0xFFAD57D9)
)

val purpleDarkPalette = baseDarkPalette.copy(
    tintColor = Color(0xFFD580FF)
)

val orangeLightPalette = baseLightPalette.copy(
    tintColor = Color(0xFFFF6619)
)

val orangeDarkPalette = baseDarkPalette.copy(
    tintColor = Color(0xFFFF974D)
)

val blueLightPalette = baseLightPalette.copy(
    tintColor = Color(0xFF4D88FF)
)

val blueDarkPalette = baseDarkPalette.copy(
    tintColor = Color(0xFF99BBFF)
)
