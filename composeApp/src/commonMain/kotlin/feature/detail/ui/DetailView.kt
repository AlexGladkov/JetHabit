package feature.detail.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import di.LocalPlatform
import di.Platform
import feature.detail.presentation.models.DetailEvent
import feature.detail.presentation.models.DetailViewState
import feature.habits.data.HabitType
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.*
import ui.themes.JetHabitTheme
import ui.themes.components.JetMenu
import utils.title

@Composable
internal fun DetailView(
    viewState: DetailViewState,
    eventHandler: (DetailEvent) -> Unit
) {
    val platform = LocalPlatform.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (platform != Platform.Android) {
                    Icon(
                        modifier = Modifier
                            .clickable { eventHandler.invoke(DetailEvent.CloseScreen) }
                            .size(56.dp).padding(16.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JetHabitTheme.colors.controlColor
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(
                            start = JetHabitTheme.shapes.padding,
                            end = JetHabitTheme.shapes.padding,
                        ),
                        text = viewState.itemTitle,
                        style = JetHabitTheme.typography.heading,
                        color = JetHabitTheme.colors.primaryText
                    )

                    if (viewState.type == HabitType.REGULAR) {
                        Text(
                            modifier = Modifier.padding(
                                start = JetHabitTheme.shapes.padding,
                                end = JetHabitTheme.shapes.padding,
                                top = 2.dp
                            ),
                            text = if (viewState.isGood) stringResource(Res.string.good_habit) else stringResource(Res.string.bad_habit),
                            style = JetHabitTheme.typography.caption,
                            color = JetHabitTheme.colors.controlColor
                        )
                    }
                }

                Icon(
                    modifier = Modifier.clickable { eventHandler.invoke(DetailEvent.DeleteItem) }
                        .size(56.dp).padding(16.dp),
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Item",
                    tint = JetHabitTheme.colors.controlColor)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Streak Statistics Section
            if (viewState.type == HabitType.REGULAR) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = JetHabitTheme.shapes.padding)
                ) {
                    Text(
                        text = stringResource(Res.string.streak_statistics),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.current_streak),
                                style = JetHabitTheme.typography.caption,
                                color = JetHabitTheme.colors.secondaryText
                            )
                            Text(
                                text = stringResource(Res.string.streak_days, viewState.currentStreak),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.tintColor
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.longest_streak),
                                style = JetHabitTheme.typography.caption,
                                color = JetHabitTheme.colors.secondaryText
                            )
                            Text(
                                text = stringResource(Res.string.streak_days, viewState.longestStreak),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.primaryText
                            )
                        }
                    }

                    if (viewState.lastCompletedDate != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.last_completed, viewState.lastCompletedDate),
                            style = JetHabitTheme.typography.caption,
                            color = JetHabitTheme.colors.secondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (viewState.type == HabitType.TRACKER) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = JetHabitTheme.shapes.padding)
                ) {
                    Text(
                        text = stringResource(Res.string.tracker_current_value),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )
                    
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = viewState.currentValue?.let { 
                            stringResource(Res.string.tracker_value_kg, it.toString())
                        } ?: stringResource(Res.string.tracker_no_value),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.tracker_new_value),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )

                    OutlinedTextField(
                        value = viewState.newValue?.toString() ?: "",
                        onValueChange = { newValue ->
                            eventHandler.invoke(DetailEvent.NewValueChanged(newValue.ifEmpty { null }))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.tracker_value_hint),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.secondaryText
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = JetHabitTheme.colors.primaryText,
                            cursorColor = JetHabitTheme.colors.tintColor,
                            focusedBorderColor = JetHabitTheme.colors.tintColor,
                            unfocusedBorderColor = JetHabitTheme.colors.secondaryText
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.tracker_measurement),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )

                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = stringResource(Res.string.tracker_measurement_kg),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (viewState.type == HabitType.REGULAR) {
                JetMenu(
                    title = stringResource(Res.string.title_start_date),
                    value = viewState.startDate.title()
                ) {
                    eventHandler.invoke(DetailEvent.StartDateClicked)
                }

                JetMenu(
                    title = stringResource(Res.string.title_end_date),
                    value = viewState.endDate.title()
                ) {
                    eventHandler.invoke(DetailEvent.EndDateClicked)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = { eventHandler.invoke(DetailEvent.SaveChanges) },
                enabled = !viewState.isDeleting,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = JetHabitTheme.colors.tintColor,
                    disabledBackgroundColor = JetHabitTheme.colors.tintColor.copy(
                        alpha = 0.3f
                    )
                )
            ) {
                Text(
                    text = stringResource(Res.string.action_save),
                    style = JetHabitTheme.typography.body,
                    color = Color.White
                )
            }
        }
    }
}