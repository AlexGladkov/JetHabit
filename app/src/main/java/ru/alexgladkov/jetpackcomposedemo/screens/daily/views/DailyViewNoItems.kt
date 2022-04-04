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
import androidx.compose.material.icons.filled.Info
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
fun DailyViewNoItems(
    onComposeClick: () -> Unit
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
                    imageVector = Icons.Filled.Info,
                    tint = JetHabitTheme.colors.controlColor,
                    contentDescription = "No Items Found"
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    text = stringResource(id = R.string.daily_no_items),
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.primaryText,
                    textAlign = TextAlign.Center
                )

                JetHabitButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = JetHabitTheme.colors.controlColor,
                    text = stringResource(id = R.string.action_add),
                    onClick = onComposeClick
                )
            }
        }
    }
}

@Preview
@Composable
fun DailyViewNoItem_Preview() {
    MainTheme {
        DailyViewNoItems(
            onComposeClick = {}
        )
    }
}