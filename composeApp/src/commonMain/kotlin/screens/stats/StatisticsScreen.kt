package screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import screens.stats.models.StatsEvent
import screens.stats.models.StatsViewState
import screens.stats.views.StatisticCell
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.title_statistics
import ui.themes.JetHabitTheme

@Composable
internal fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel { StatisticsViewModel() }
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    StatisticsView(viewState)

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(StatsEvent.ReloadScreen)
    }
}

@Composable
internal fun StatisticsView(viewState: StatsViewState) {
    Column(modifier = Modifier.background(JetHabitTheme.colors.primaryBackground).fillMaxSize()) {
        Text(
            modifier = Modifier.padding(
                start = JetHabitTheme.shapes.padding,
                end = JetHabitTheme.shapes.padding,
                top = JetHabitTheme.shapes.padding + 8.dp
            ),
            text = stringResource(Res.string.title_statistics),
            style = JetHabitTheme.typography.heading,
            color = JetHabitTheme.colors.primaryText
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            viewState.activeProgress.forEach {
                item {
                    StatisticCell(it)
                }
            }
        }
    }
}