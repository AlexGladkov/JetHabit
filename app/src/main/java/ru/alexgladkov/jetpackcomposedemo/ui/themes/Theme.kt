package ru.alexgladkov.jetpackcomposedemo.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alexgladkov.jetpackcomposedemo.R

@Composable
fun MainTheme(
    style: JetHabbitStyle = JetHabbitStyle.Purple,
    textSize: JetHabbitSize = JetHabbitSize.Medium,
    paddingSize: JetHabbitSize = JetHabbitSize.Medium,
    corners: JetHabbitCorners = JetHabbitCorners.Rounded,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (darkTheme) {
        true -> {
            when (style) {
                JetHabbitStyle.Purple -> purpleDarkPalette
                JetHabbitStyle.Blue -> blueDarkPalette
                JetHabbitStyle.Orange -> orangeDarkPalette
                JetHabbitStyle.Red -> redDarkPalette
                JetHabbitStyle.Green -> greenDarkPalette
            }
        }
        false -> {
            when (style) {
                JetHabbitStyle.Purple -> purpleLightPalette
                JetHabbitStyle.Blue -> blueLightPalette
                JetHabbitStyle.Orange -> orangeLightPalette
                JetHabbitStyle.Red -> redLightPalette
                JetHabbitStyle.Green -> greenLightPalette
            }
        }
    }

    val typography = JetHabbitTypography(
        heading = TextStyle(
            fontSize = when (textSize) {
                JetHabbitSize.Small -> 24.sp
                JetHabbitSize.Medium -> 28.sp
                JetHabbitSize.Big -> 32.sp
            },
            fontWeight = FontWeight.Bold
        ),
        body = TextStyle(
            fontSize = when (textSize) {
                JetHabbitSize.Small -> 14.sp
                JetHabbitSize.Medium -> 16.sp
                JetHabbitSize.Big -> 18.sp
            },
            fontWeight = FontWeight.Normal
        ),
        toolbar = TextStyle(
            fontSize = when (textSize) {
                JetHabbitSize.Small -> 14.sp
                JetHabbitSize.Medium -> 16.sp
                JetHabbitSize.Big -> 18.sp
            },
            fontWeight = FontWeight.Medium
        ),
        caption = TextStyle(
            fontSize = when (textSize) {
                JetHabbitSize.Small -> 10.sp
                JetHabbitSize.Medium -> 12.sp
                JetHabbitSize.Big -> 14.sp
            }
        )
    )

    val shapes = JetHabbitShape(
        padding = when (paddingSize) {
            JetHabbitSize.Small -> 12.dp
            JetHabbitSize.Medium -> 16.dp
            JetHabbitSize.Big -> 20.dp
        },
        cornersStyle = when (corners) {
            JetHabbitCorners.Flat -> RoundedCornerShape(0.dp)
            JetHabbitCorners.Rounded -> RoundedCornerShape(8.dp)
        }
    )

    val images = JetHabbitImage(
        mainIcon = if (darkTheme) R.drawable.ic_baseline_mood_24 else R.drawable.ic_baseline_mood_bad_24,
        mainIconDescription = if (darkTheme) "Good Mood" else "Bad Mood"
    )

    CompositionLocalProvider(
        LocalJetHabbitColors provides colors,
        LocalJetHabbitTypography provides typography,
        LocalJetHabbitShape provides shapes,
        LocalJetHabbitImage provides images,
        content = content
    )
}