package feature.health.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import feature.health.presentation.HealthViewModel
import feature.health.ui.views.TrackerHistoryItem
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.health_title
import ui.themes.JetHabitTheme

@Composable
fun HealthScreen(
    viewModel: HealthViewModel = viewModel()
) {
    val viewState by viewModel.viewStates().collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(Res.string.health_title),
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText,
                modifier = Modifier.padding(
                    horizontal = JetHabitTheme.shapes.padding,
                    vertical = JetHabitTheme.shapes.padding + 8.dp
                )
            )

            LazyColumn {
                items(viewState.trackerHistory) { item ->
                    TrackerHistoryItem(item)
                }
            }
        }
    }
} 