package ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun MainTheme(
    style: JetHabitStyle = JetHabitStyle.Purple,
    textSize: JetHabitSize = JetHabitSize.Medium,
    paddingSize: JetHabitSize = JetHabitSize.Medium,
    corners: JetHabitCorners = JetHabitCorners.Rounded,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (darkTheme) {
        true -> {
            when (style) {
                JetHabitStyle.Purple -> purpleDarkPalette
                JetHabitStyle.Blue -> blueDarkPalette
                JetHabitStyle.Orange -> orangeDarkPalette
                JetHabitStyle.Red -> redDarkPalette
                JetHabitStyle.Green -> greenDarkPalette
            }
        }
        false -> {
            when (style) {
                JetHabitStyle.Purple -> purpleLightPalette
                JetHabitStyle.Blue -> blueLightPalette
                JetHabitStyle.Orange -> orangeLightPalette
                JetHabitStyle.Red -> redLightPalette
                JetHabitStyle.Green -> greenLightPalette
            }
        }
    }

    val typography = JetHabitTypography(
        heading = TextStyle(
            fontSize = when (textSize) {
                JetHabitSize.Small -> 24.sp
                JetHabitSize.Medium -> 28.sp
                JetHabitSize.Big -> 32.sp
            },
            fontWeight = FontWeight.Bold
        ),
        body = TextStyle(
            fontSize = when (textSize) {
                JetHabitSize.Small -> 14.sp
                JetHabitSize.Medium -> 16.sp
                JetHabitSize.Big -> 18.sp
            },
            fontWeight = FontWeight.Normal
        ),
        toolbar = TextStyle(
            fontSize = when (textSize) {
                JetHabitSize.Small -> 14.sp
                JetHabitSize.Medium -> 16.sp
                JetHabitSize.Big -> 18.sp
            },
            fontWeight = FontWeight.Medium
        ),
        caption = TextStyle(
            fontSize = when (textSize) {
                JetHabitSize.Small -> 10.sp
                JetHabitSize.Medium -> 12.sp
                JetHabitSize.Big -> 14.sp
            }
        )
    )

    val shapes = JetHabitShape(
        padding = when (paddingSize) {
            JetHabitSize.Small -> 12.dp
            JetHabitSize.Medium -> 16.dp
            JetHabitSize.Big -> 20.dp
        },
        cornersStyle = when (corners) {
            JetHabitCorners.Flat -> RoundedCornerShape(0.dp)
            JetHabitCorners.Rounded -> RoundedCornerShape(8.dp)
        }
    )

    val images = JetHabitImage(
        mainIcon =  null, //if (darkTheme) R.drawable.ic_baseline_mood_24 else R.drawable.ic_baseline_mood_bad_24,
        mainIconDescription = if (darkTheme) "Good Mood" else "Bad Mood"
    )

    CompositionLocalProvider(
        LocalJetHabitColors provides colors,
        LocalJetHabitTypography provides typography,
        LocalJetHabitShape provides shapes,
        LocalJetHabitImage provides images,
        content = content
    )
}