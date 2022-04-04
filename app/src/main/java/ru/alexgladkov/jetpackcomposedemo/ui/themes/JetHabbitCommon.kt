package ru.alexgladkov.jetpackcomposedemo.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class JetHabitColors(
    val primaryText: Color,
    val primaryBackground: Color,
    val secondaryText: Color,
    val secondaryBackground: Color,
    val tintColor: Color,
    val controlColor: Color,
    val errorColor: Color
)

data class JetHabitTypography(
    val heading: TextStyle,
    val body: TextStyle,
    val toolbar: TextStyle,
    val caption: TextStyle
)

data class JetHabitShape(
    val padding: Dp,
    val cornersStyle: Shape
)

data class JetHabitImage(
    val mainIcon: Int,
    val mainIconDescription: String
)

object JetHabitTheme {
    val colors: JetHabitColors
        @Composable
        get() = LocalJetHabitColors.current

    val typography: JetHabitTypography
        @Composable
        get() = LocalJetHabitTypography.current

    val shapes: JetHabitShape
        @Composable
        get() = LocalJetHabitShape.current

    val images: JetHabitImage
        @Composable
        get() = LocalJetHabitImage.current
}

data class JetHabitThemeSettings(
    val style: JetHabitStyle,
    val textSize: JetHabitSize,
    val paddingSize: JetHabitSize,
    val corners: JetHabitCorners,
    val isDarkMode: Boolean,
)

enum class JetHabitStyle {
    Purple, Orange, Blue, Red, Green
}

enum class JetHabitSize {
    Small, Medium, Big
}

enum class JetHabitCorners {
    Flat, Rounded
}

val LocalJetHabitColors = staticCompositionLocalOf<JetHabitColors> {
    error("No colors provided")
}

val LocalJetHabitTypography = staticCompositionLocalOf<JetHabitTypography> {
    error("No font provided")
}

val LocalJetHabitShape = staticCompositionLocalOf<JetHabitShape> {
    error("No shapes provided")
}

val LocalJetHabitImage = staticCompositionLocalOf<JetHabitImage> {
    error("No images provided")
}