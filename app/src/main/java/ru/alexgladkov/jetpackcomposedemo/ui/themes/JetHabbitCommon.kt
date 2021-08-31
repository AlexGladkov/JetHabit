package ru.alexgladkov.jetpackcomposedemo.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class JetHabbitColors(
    val primaryText: Color,
    val primaryBackground: Color,
    val secondaryText: Color,
    val secondaryBackground: Color,
    val tintColor: Color
)

data class JetHabbitTypography(
    val heading: TextStyle,
    val body: TextStyle,
    val toolbar: TextStyle
)

data class JetHabbitShape(
    val padding: Dp,
    val cornersStyle: Shape
)

data class JetHabbitImage(
    val mainIcon: Int,
    val mainIconDescription: String
)

object JetHabbitTheme {
    val colors: JetHabbitColors
        @Composable
        get() = LocalJetHabbitColors.current

    val typography: JetHabbitTypography
        @Composable
        get() = LocalJetHabbitTypography.current

    val shapes: JetHabbitShape
        @Composable
        get() = LocalJetHabbitShape.current

    val images: JetHabbitImage
        @Composable
        get() = LocalJetHabbitImage.current
}

enum class JetHabbitStyle {
    Purple, Orange, Blue, Red, Green
}

enum class JetHabbitSize {
    Small, Medium, Big
}

enum class JetHabbitCorners {
    Flat, Rounded
}

val LocalJetHabbitColors = staticCompositionLocalOf<JetHabbitColors> {
    error("No colors provided")
}

val LocalJetHabbitTypography = staticCompositionLocalOf<JetHabbitTypography> {
    error("No font provided")
}

val LocalJetHabbitShape = staticCompositionLocalOf<JetHabbitShape> {
    error("No shapes provided")
}

val LocalJetHabbitImage = staticCompositionLocalOf<JetHabbitImage> {
    error("No images provided")
}