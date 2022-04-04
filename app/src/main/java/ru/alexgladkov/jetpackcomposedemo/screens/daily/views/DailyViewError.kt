package ru.alexgladkov.jetpackcomposedemo.screens.daily.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.components.JetHabitButton

@Composable
fun DailyViewError(
    onReloadClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp).align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(96.dp),
                    imageVector = Icons.Filled.Warning,
                    tint = JetHabitTheme.colors.controlColor,
                    contentDescription = "Error loading items"
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    text = stringResource(id = R.string.daily_error_loading),
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.primaryText,
                    textAlign = TextAlign.Center
                )

                JetHabitButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = JetHabitTheme.colors.controlColor,
                    text = stringResource(id = R.string.action_refresh),
                    onClick = onReloadClick
                )
            }
        }
    }
}

@Composable
@Preview
fun DailyViewError_Preview() {
    MainTheme {
       DailyViewError(onReloadClick = {})
    }
}