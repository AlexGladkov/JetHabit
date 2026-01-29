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
import feature.statistics.ui.models.StatisticsTab
import feature.statistics.ui.views.StatisticsItem
import feature.statistics.ui.views.StatisticsViewNoItems
import feature.statistics.ui.views.WeeklyStatisticsView
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

            // Tab Row
            TabRow(
                selectedTabIndex = if (viewState.activeTab == StatisticsTab.WEEKLY) 0 else 1,
                backgroundColor = JetHabitTheme.colors.primaryBackground,
                contentColor = JetHabitTheme.colors.tintColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = viewState.activeTab == StatisticsTab.WEEKLY,
                    onClick = { viewModel.obtainEvent(StatisticsEvent.SwitchTab(StatisticsTab.WEEKLY)) },
                    text = {
                        Text(
                            text = "Weekly",
                            style = JetHabitTheme.typography.toolbar,
                            color = if (viewState.activeTab == StatisticsTab.WEEKLY) {
                                JetHabitTheme.colors.tintColor
                            } else {
                                JetHabitTheme.colors.secondaryText
                            }
                        )
                    }
                )
                Tab(
                    selected = viewState.activeTab == StatisticsTab.ALL_TIME,
                    onClick = { viewModel.obtainEvent(StatisticsEvent.SwitchTab(StatisticsTab.ALL_TIME)) },
                    text = {
                        Text(
                            text = "All Time",
                            style = JetHabitTheme.typography.toolbar,
                            color = if (viewState.activeTab == StatisticsTab.ALL_TIME) {
                                JetHabitTheme.colors.tintColor
                            } else {
                                JetHabitTheme.colors.secondaryText
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = JetHabitTheme.colors.tintColor)
                }
            } else {
                when (viewState.activeTab) {
                    StatisticsTab.WEEKLY -> {
                        if (!viewState.hasData || viewState.weeklyData == null) {
                            StatisticsViewNoItems()
                        } else {
                            WeeklyStatisticsView(
                                weeklyData = viewState.weeklyData,
                                onPreviousWeek = { viewModel.obtainEvent(StatisticsEvent.PreviousWeek) },
                                onNextWeek = { viewModel.obtainEvent(StatisticsEvent.NextWeek) }
                            )
                        }
                    }
                    StatisticsTab.ALL_TIME -> {
                        if (!viewState.hasData) {
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
        }
    }
} 