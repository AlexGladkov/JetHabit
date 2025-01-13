package feature.health.track.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import feature.health.track.presentation.TrackHabitViewModel
import feature.health.track.presentation.models.TrackHabitAction
import feature.health.track.presentation.models.TrackHabitEvent
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.action_save
import tech.mobiledeveloper.jethabit.resources.tracker_date
import tech.mobiledeveloper.jethabit.resources.tracker_new_value
import tech.mobiledeveloper.jethabit.resources.tracker_select_date
import ui.themes.JetHabitTheme
import themes.components.CCalendar
import ui.themes.components.JetHabitButton

@Composable
fun TrackHabitScreen(
    habitId: String,
    navController: NavController
) {
    val viewModel = remember { TrackHabitViewModel(habitId) }
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)
    var showCalendar by remember { mutableStateOf(false) }

    LaunchedEffect(viewAction) {
        when (viewAction) {
            TrackHabitAction.NavigateBack -> {
                navController.popBackStack()
                viewModel.clearAction()
            }
            null -> {}
        }
    }

    if (showCalendar) {
        AlertDialog(
            onDismissRequest = { showCalendar = false },
            text = {
                CCalendar(
                    selectedDate = LocalDate.parse(viewState.selectedDate),
                    textColor = JetHabitTheme.colors.primaryText,
                    dayOfWeekColor = JetHabitTheme.colors.secondaryText,
                    selectedColor = JetHabitTheme.colors.tintColor,
                    allowSameDate = true,
                    onDateSelected = { date ->
                        viewModel.obtainEvent(TrackHabitEvent.DateSelected(date.toString()))
                        showCalendar = false
                    }
                )
            },
            buttons = { },
            backgroundColor = JetHabitTheme.colors.primaryBackground,
            contentColor = JetHabitTheme.colors.primaryText
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(JetHabitTheme.shapes.padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewState.title,
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewState.selectedDate,
                    onValueChange = { },
                    label = { 
                        Text(
                            text = stringResource(Res.string.tracker_date),
                            color = JetHabitTheme.colors.secondaryText
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = JetHabitTheme.typography.body.copy(color = JetHabitTheme.colors.primaryText),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showCalendar = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(Res.string.tracker_select_date),
                                tint = JetHabitTheme.colors.primaryText
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewState.newValue?.toString() ?: "",
                    onValueChange = { viewModel.obtainEvent(TrackHabitEvent.NewValueChanged(it)) },
                    label = { 
                        Text(
                            text = stringResource(Res.string.tracker_new_value, viewState.measurement.toString()),
                            color = JetHabitTheme.colors.secondaryText
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = JetHabitTheme.typography.body.copy(color = JetHabitTheme.colors.primaryText),
                    isError = viewState.error != null
                )

                viewState.error?.let { errorResId ->
                    Text(
                        text = stringResource(errorResId),
                        color = JetHabitTheme.colors.errorColor,
                        style = JetHabitTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            JetHabitButton(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = JetHabitTheme.colors.tintColor,
                text = stringResource(Res.string.action_save),
                onClick = { viewModel.obtainEvent(TrackHabitEvent.SaveClicked) }
            )
        }
    }
} 