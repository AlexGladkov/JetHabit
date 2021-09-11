package ru.alexgladkov.jetpackcomposedemo.screens.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme

@ExperimentalFoundationApi
@Composable
fun ComposeScreen(
    modifier: Modifier,
    composeViewModel: ComposeViewModel
) {
    val titleValue = remember { mutableStateOf("") }
    val isGoodValue = remember { mutableStateOf(false) }

    val viewState = composeViewModel.composeViewState.observeAsState(СomposeViewState())

    Surface(modifier = modifier.fillMaxSize(), color = JetHabbitTheme.colors.primaryBackground) {
        Box {
            LazyColumn(
                Modifier.background(JetHabbitTheme.colors.primaryBackground),
                content = {
                    stickyHeader {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = JetHabbitTheme.shapes.padding,
                                vertical = JetHabbitTheme.shapes.padding + 8.dp
                            ),
                            text = stringResource(id = R.string.compose_new_record),
                            style = JetHabbitTheme.typography.heading,
                            color = JetHabbitTheme.colors.primaryText
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Text(
                                text = stringResource(id = R.string.compose_title),
                                style = JetHabbitTheme.typography.caption,
                                color = JetHabbitTheme.colors.secondaryText
                            )

                            TextField(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxWidth(),
                                singleLine = true,
                                value = titleValue.value, onValueChange = { titleValue.value = it },
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = JetHabbitTheme.colors.primaryBackground,
                                    textColor = JetHabbitTheme.colors.primaryText,
                                    focusedIndicatorColor = JetHabbitTheme.colors.tintColor,
                                    disabledIndicatorColor = JetHabbitTheme.colors.controlColor,
                                    cursorColor = JetHabbitTheme.colors.tintColor
                                )
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                            Text(
                                modifier = Modifier.padding(end = 16.dp),
                                text = stringResource(id = R.string.compose_is_good),
                                style = JetHabbitTheme.typography.body,
                                color = JetHabbitTheme.colors.primaryText
                            )

                            Checkbox(
                                checked = isGoodValue.value, onCheckedChange = { isGoodValue.value = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = JetHabbitTheme.colors.tintColor,
                                    uncheckedColor = JetHabbitTheme.colors.secondaryText
                                )
                            )
                        }
                    }

                    item {
                        Button(
                            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
                                .height(48.dp).fillMaxWidth(),
                            onClick = {

                            }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = JetHabbitTheme.colors.tintColor
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.action_add),
                                style = JetHabbitTheme.typography.body,
                                color = Color.White
                            )
                        }
                    }
                })
        }
    }
}