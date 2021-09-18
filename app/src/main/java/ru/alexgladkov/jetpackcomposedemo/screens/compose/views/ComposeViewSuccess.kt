package ru.alexgladkov.jetpackcomposedemo.screens.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

@Composable
fun ComposeViewSuccess(modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = JetHabbitTheme.colors.primaryBackground
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(90.dp)
                        .align(CenterHorizontally),
                    imageVector = Icons.Filled.CheckCircle,
                    tint = JetHabbitTheme.colors.controlColor,
                    contentDescription = "Accepted Icon"
                )

                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = stringResource(id = R.string.compose_success_add),
                    style = JetHabbitTheme.typography.body,
                    color = JetHabbitTheme.colors.primaryText
                )

                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(48.dp)
                        .fillMaxWidth(),
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = JetHabbitTheme.colors.controlColor,
                        disabledBackgroundColor = JetHabbitTheme.colors.controlColor.copy(
                            alpha = 0.3f
                        )
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.action_close),
                        style = JetHabbitTheme.typography.body,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ComposeViewSuccess_Preview() {
    MainTheme(darkTheme = true) {
        ComposeViewSuccess(onCloseClick = {})
    }
}