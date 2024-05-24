package feature.create.ui

import PreviewApp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState
import screens.compose.views.ComposeViewInitialError
import tech.mobiledeveloper.jethabit.app.AppRes
import ui.themes.JetHabitTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeView(
    viewState: ComposeViewState,
    eventHandler: (ComposeEvent) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = JetHabitTheme.colors.primaryBackground) {
        Box {
            LazyColumn(
                Modifier.background(JetHabitTheme.colors.primaryBackground),
                content = {
                    stickyHeader {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = JetHabitTheme.shapes.padding,
                                vertical = JetHabitTheme.shapes.padding + 8.dp
                            ),
                            text = AppRes.string.compose_new_record,
                            style = JetHabitTheme.typography.heading,
                            color = JetHabitTheme.colors.primaryText
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Text(
                                text = AppRes.string.compose_title,
                                style = JetHabitTheme.typography.caption,
                                color = JetHabitTheme.colors.secondaryText
                            )

                            TextField(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxWidth(),
                                singleLine = true,
                                enabled = !viewState.isSending,
                                value = viewState.habitTitle,
                                trailingIcon = {
                                    if (viewState.habitTitle.isNotBlank()) {
                                        Icon(
                                            modifier = Modifier.clickable {
                                                eventHandler.invoke(ComposeEvent.ClearClicked)
                                            },
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Close",
                                            tint = JetHabitTheme.colors.controlColor
                                        )
                                    }
                                },
                                onValueChange = {
                                    eventHandler.invoke(ComposeEvent.TitleChanged(it))
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = JetHabitTheme.colors.primaryBackground,
                                    textColor = JetHabitTheme.colors.primaryText,
                                    focusedIndicatorColor = JetHabitTheme.colors.tintColor,
                                    disabledIndicatorColor = JetHabitTheme.colors.controlColor,
                                    cursorColor = JetHabitTheme.colors.tintColor
                                )
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 16.dp),
                                text = AppRes.string.compose_is_good,
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.primaryText
                            )

                            Checkbox(
                                checked = viewState.isGoodHabit,
                                enabled = !viewState.isSending,
                                onCheckedChange = { eventHandler.invoke(ComposeEvent.CheckboxClicked(it)) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = JetHabitTheme.colors.tintColor,
                                    uncheckedColor = JetHabitTheme.colors.secondaryText,
                                    disabledColor = JetHabitTheme.colors.tintColor.copy(
                                        alpha = 0.3f
                                    )
                                )
                            )
                        }
                    }

                    item {
                        Button(
                            modifier = Modifier
                                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                                .height(48.dp)
                                .fillMaxWidth(),
                            onClick = { eventHandler.invoke(ComposeEvent.SaveClicked) },
                            enabled = !viewState.isSending && viewState.habitTitle.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = JetHabitTheme.colors.tintColor,
                                disabledBackgroundColor = JetHabitTheme.colors.tintColor.copy(
                                    alpha = 0.3f
                                )
                            )
                        ) {
                            if (viewState.isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = AppRes.string.action_add,
                                    style = JetHabitTheme.typography.body,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            modifier = Modifier
                                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                                .height(48.dp)
                                .fillMaxWidth(),
                            onClick = { eventHandler.invoke(ComposeEvent.CloseClicked) },
                            enabled = !viewState.isSending,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = JetHabitTheme.colors.controlColor,
                                disabledBackgroundColor = JetHabitTheme.colors.controlColor.copy(
                                    alpha = 0.3f
                                )
                            )
                        ) {
                            Text(
                                text = AppRes.string.action_close,
                                style = JetHabitTheme.typography.body,
                                color = Color.White
                            )
                        }
                    }

                    viewState.sendingError?.let { error ->
                        item {
                            ComposeViewInitialError(error = error)
                        }
                    }
                })
        }
    }
}

@Composable
@Preview
fun ComposeView_Preview() {
    PreviewApp {
        ComposeView(
            viewState = ComposeViewState()
        ) {
            
        }
    }
}