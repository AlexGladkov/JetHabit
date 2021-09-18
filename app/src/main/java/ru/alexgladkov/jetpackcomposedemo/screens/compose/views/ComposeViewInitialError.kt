package ru.alexgladkov.jetpackcomposedemo.screens.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeError
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

@Composable
fun ComposeViewInitialError(error: ComposeError) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(16.dp).align(Alignment.Center),
            text = when (error) {
                ComposeError.SendingGeneric -> stringResource(id = R.string.error_new_habbit)
            },
            color = JetHabbitTheme.colors.errorColor
        )
    }
}

@Preview
@Composable
fun ComposeViewInitialErrorView_Preview() {
    MainTheme(darkTheme = true) {
        ComposeViewInitialError(error = ComposeError.SendingGeneric)
    }
}