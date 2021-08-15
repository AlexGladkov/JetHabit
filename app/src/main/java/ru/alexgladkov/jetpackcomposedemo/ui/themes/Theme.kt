package ru.alexgladkov.jetpackcomposedemo.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MainTheme(
    style: JetHabbitStyle = JetHabbitStyle.Purple,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (darkTheme) {
        true -> {
            when (style) {
                JetHabbitStyle.Purple -> purpleDarkPalette
                JetHabbitStyle.Blue -> blueDarkPalette
                JetHabbitStyle.Orange -> orangeDarkPalette
            }
        }
        false -> {
            when (style) {
                JetHabbitStyle.Purple -> purpleLightPalette
                JetHabbitStyle.Blue -> blueLightPalette
                JetHabbitStyle.Orange -> orangeLightPalette
            }
        }
    }

    CompositionLocalProvider(LocalJetHabbitColors provides colors, content = content)
}