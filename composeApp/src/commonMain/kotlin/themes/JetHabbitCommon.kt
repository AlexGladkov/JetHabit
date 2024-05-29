package ui.themes

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
    val errorColor: Color,
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
    val mainIcon: Int?,
    val mainIconDescription: String
)

object JetHabitTheme {
    internal val colors: JetHabitColors
        @Composable
        internal get() = LocalJetHabitColors.current

    internal val typography: JetHabitTypography
        @Composable
        internal get() = LocalJetHabitTypography.current

    internal val shapes: JetHabitShape
        @Composable
        internal get() = LocalJetHabitShape.current

    internal val images: JetHabitImage
        @Composable
        internal get() = LocalJetHabitImage.current
}

enum class JetHabitStyle {
    Purple, Orange, Blue, Red, Green
}

enum class JetHabitSize {
    Small, Medium, Big
}

enum class JetHabitCorners {
    Flat, Rounded
}

internal val LocalJetHabitColors = staticCompositionLocalOf<JetHabitColors> {
    error("No colors provided")
}

internal val LocalJetHabitTypography = staticCompositionLocalOf<JetHabitTypography> {
    error("No font provided")
}

internal val LocalJetHabitShape = staticCompositionLocalOf<JetHabitShape> {
    error("No shapes provided")
}

internal val LocalJetHabitImage = staticCompositionLocalOf<JetHabitImage> {
    error("No images provided")
}