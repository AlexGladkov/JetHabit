package feature.statistics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.statistics.presentation.StatisticsViewModel
import feature.statistics.ui.models.StatisticsEvent
import feature.statistics.ui.views.StatisticsItem
import feature.statistics.ui.views.StatisticsViewNoItems
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.title_statistics
import ui.themes.JetHabitTheme

@Composable
fun StatisticsScreen() {
    val viewModel = remember { StatisticsViewModel() }
    val viewState by viewModel.viewStates().collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(StatisticsEvent.LoadStatistics)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(JetHabitTheme.shapes.padding)
        ) {
            Text(
                text = stringResource(Res.string.title_statistics),
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (viewState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = JetHabitTheme.colors.tintColor)
                }
            } else if (!viewState.hasData) {
                StatisticsViewNoItems()
            } else {
                LazyColumn {
                    items(viewState.statistics) { stat ->
                        StatisticsItem(
                            title = stat.title,
                            completionRate = stat.completionRate,
                            trackedDays = stat.trackedDays
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
} 