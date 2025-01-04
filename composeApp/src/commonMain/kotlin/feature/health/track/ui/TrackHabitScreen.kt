package feature.health.track.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.health.track.presentation.TrackHabitViewModel
import feature.health.track.presentation.models.TrackHabitEvent
import ui.themes.JetHabitTheme

@Composable
fun TrackHabitScreen(
    habitId: String,
    onClose: () -> Unit
) {
    val viewModel = remember { TrackHabitViewModel(habitId) }
    val viewState by viewModel.viewStates().collectAsState()

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

            OutlinedTextField(
                value = viewState.newValue?.toString() ?: "",
                onValueChange = { viewModel.obtainEvent(TrackHabitEvent.NewValueChanged(it)) },
                label = { 
                    Text(
                        text = "New Value (${viewState.measurement})",
                        color = JetHabitTheme.colors.secondaryText
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = JetHabitTheme.typography.body.copy(color = JetHabitTheme.colors.primaryText)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    viewModel.obtainEvent(TrackHabitEvent.SaveClicked)
                    onClose()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
} 