package screens.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adeo.kviewmodel.odyssey.StoredViewModel
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import screens.stats.models.StatsEvent
import screens.stats.models.StatsViewState
import screens.stats.views.StatisticCell
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme

@Composable
internal fun StatisticsScreen() {
    val rootController = LocalRootController.current

    StoredViewModel(factory = { StatisticsViewModel() }) { viewModel ->
        val viewState by viewModel.viewStates().collectAsState()
        val viewAction by viewModel.viewActions().collectAsState(null)

        StatisticsView(viewState)

        LaunchedEffect(Unit) {
            viewModel.obtainEvent(StatsEvent.ReloadScreen)
        }
    }
}

@Composable
internal fun StatisticsView(viewState: StatsViewState) {
    Column {
        Text(
            modifier = Modifier.padding(
                start = JetHabitTheme.shapes.padding,
                end = JetHabitTheme.shapes.padding,
                top = JetHabitTheme.shapes.padding + 8.dp
            ),
            text = AppRes.string.title_statistics,
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