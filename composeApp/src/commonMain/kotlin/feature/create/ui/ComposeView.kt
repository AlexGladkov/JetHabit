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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import feature.habits.data.HabitType
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import screens.compose.views.ComposeViewInitialError
import tech.mobiledeveloper.jethabit.resources.*
import ui.themes.JetHabitTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import feature.create.presentation.models.ComposeEvent
import feature.create.presentation.models.ComposeViewState
import feature.habits.data.Measurement
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import themes.components.CCalendar

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
                            text = stringResource(Res.string.compose_new_record),
                            style = JetHabitTheme.typography.heading,
                            color = JetHabitTheme.colors.primaryText
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = JetHabitTheme.shapes.padding
                            )
                        ) {
                            Text(
                                text = stringResource(Res.string.compose_title),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.primaryText
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
                        Column(
                            modifier = Modifier.padding(
                                horizontal = JetHabitTheme.shapes.padding,
                                vertical = JetHabitTheme.shapes.padding
                            )
                        ) {
                            Text(
                                text = stringResource(Res.string.compose_habit_type),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.primaryText
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HabitType.values().forEach { type ->
                                    OutlinedButton(
                                        onClick = { eventHandler.invoke(ComposeEvent.TypeSelected(type)) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            backgroundColor = if (viewState.habitType == type) 
                                                JetHabitTheme.colors.tintColor 
                                            else 
                                                Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = when (type) {
                                                HabitType.REGULAR -> stringResource(Res.string.compose_habit_type_regular)
                                                HabitType.TRACKER -> stringResource(Res.string.compose_habit_type_tracker)
                                            },
                                            color = if (viewState.habitType == type)
                                                Color.White
                                            else
                                                JetHabitTheme.colors.primaryText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        if (viewState.habitType == HabitType.TRACKER) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = JetHabitTheme.shapes.padding,
                                    vertical = JetHabitTheme.shapes.padding
                                )
                            ) {
                                Text(
                                    text = stringResource(Res.string.measurement_label),
                                    style = JetHabitTheme.typography.body,
                                    color = JetHabitTheme.colors.primaryText
                                )

                                var expanded by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            backgroundColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = viewState.measurement?.let { 
                                                stringResource(it.stringRes)
                                            } ?: stringResource(Res.string.measurement_select),
                                            color = JetHabitTheme.colors.primaryText
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(JetHabitTheme.colors.primaryBackground)
                                    ) {
                                        Measurement.entries.forEach { measurement ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    eventHandler.invoke(ComposeEvent.MeasurementSelected(measurement))
                                                    expanded = false
                                                },
                                                modifier = Modifier.background(JetHabitTheme.colors.primaryBackground)
                                            ) {
                                                Text(
                                                    text = stringResource(measurement.stringRes),
                                                    color = JetHabitTheme.colors.primaryText,
                                                    style = JetHabitTheme.typography.body
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        if (viewState.habitType == HabitType.REGULAR) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = JetHabitTheme.shapes.padding,
                                    vertical = JetHabitTheme.shapes.padding
                                )
                            ) {
                                Text(
                                    text = stringResource(Res.string.compose_time_distance),
                                    style = JetHabitTheme.typography.body,
                                    color = JetHabitTheme.colors.primaryText
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Start date
                                OutlinedButton(
                                    onClick = { eventHandler.invoke(ComposeEvent.ShowStartDatePicker) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        backgroundColor = Color.Transparent
                                    )
                                ) {
                                    Text(
                                        text = viewState.startDate?.toString() ?: stringResource(Res.string.compose_select_start_date),
                                        color = JetHabitTheme.colors.primaryText
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // End date
                                OutlinedButton(
                                    onClick = { eventHandler.invoke(ComposeEvent.ShowEndDatePicker) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        backgroundColor = Color.Transparent
                                    )
                                ) {
                                    Text(
                                        text = viewState.endDate?.toString() ?: stringResource(Res.string.compose_select_end_date),
                                        color = JetHabitTheme.colors.primaryText
                                    )
                                }
                            }

                            // Date pickers
                            if (viewState.showStartDatePicker) {
                                AlertDialog(
                                    onDismissRequest = { eventHandler.invoke(ComposeEvent.HideStartDatePicker) },
                                    text = {
                                        CCalendar(
                                            selectedDate = viewState.startDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                                            textColor = JetHabitTheme.colors.primaryText,
                                            dayOfWeekColor = JetHabitTheme.colors.secondaryText,
                                            selectedColor = JetHabitTheme.colors.tintColor,
                                            allowSameDate = true,
                                            onDateSelected = { date ->
                                                eventHandler.invoke(ComposeEvent.StartDateSelected(date))
                                            }
                                        )
                                    },
                                    buttons = { },
                                    backgroundColor = JetHabitTheme.colors.primaryBackground,
                                    contentColor = JetHabitTheme.colors.primaryText
                                )
                            }

                            if (viewState.showEndDatePicker) {
                                AlertDialog(
                                    onDismissRequest = { eventHandler.invoke(ComposeEvent.HideEndDatePicker) },
                                    text = {
                                        CCalendar(
                                            selectedDate = viewState.endDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                                            textColor = JetHabitTheme.colors.primaryText,
                                            dayOfWeekColor = JetHabitTheme.colors.secondaryText,
                                            selectedColor = JetHabitTheme.colors.tintColor,
                                            allowSameDate = true,
                                            onDateSelected = { date ->
                                                eventHandler.invoke(ComposeEvent.EndDateSelected(date))
                                            }
                                        )
                                    },
                                    buttons = { },
                                    backgroundColor = JetHabitTheme.colors.primaryBackground,
                                    contentColor = JetHabitTheme.colors.primaryText
                                )
                            }
                        }
                    }

                    item {
                        if (viewState.habitType == HabitType.REGULAR) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = JetHabitTheme.shapes.padding)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(Res.string.good_habit),
                                    style = JetHabitTheme.typography.body,
                                    color = JetHabitTheme.colors.primaryText
                                )

                                Switch(
                                    checked = viewState.isGoodHabit,
                                    onCheckedChange = {
                                        eventHandler.invoke(ComposeEvent.CheckboxClicked(it))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = JetHabitTheme.colors.tintColor,
                                        checkedTrackColor = JetHabitTheme.colors.tintColor.copy(alpha = 0.5f),
                                        uncheckedThumbColor = JetHabitTheme.colors.controlColor,
                                        uncheckedTrackColor = JetHabitTheme.colors.controlColor.copy(alpha = 0.5f)
                                    )
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
                                    text = stringResource(Res.string.action_add),
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
                                text = stringResource(Res.string.action_close),
                                style = JetHabitTheme.typography.body,
                                color = Color.White
                            )
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