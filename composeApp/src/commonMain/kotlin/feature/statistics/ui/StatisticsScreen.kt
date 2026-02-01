package feature.statistics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.platform.ShareImage
import core.platform.composableToBitmap
import di.Inject
import feature.statistics.presentation.StatisticsViewModel
import feature.statistics.presentation.models.StreakData
import feature.statistics.ui.models.StatisticsEvent
import feature.statistics.ui.views.StatisticsItem
import feature.statistics.ui.views.StatisticsViewNoItems
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import screens.stats.models.HabitStatistics
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.title_statistics
import ui.themes.JetHabitTheme

@Composable
fun StatisticsScreen() {
    val viewModel = remember { StatisticsViewModel() }
    val viewState by viewModel.viewStates().collectAsState()

    var shareDialogHabit by remember { mutableStateOf<HabitStatistics?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

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
                            trackedDays = stat.trackedDays,
                            currentStreak = stat.currentStreak,
                            onShareClick = if (stat.currentStreak > 0) {
                                { shareDialogHabit = stat }
                            } else null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Share dialog
        shareDialogHabit?.let { habit ->
            val streakData = StreakData(
                habitTitle = habit.title,
                streakCount = habit.currentStreak,
                habitId = habit.id
            )

            ShareStreakDialog(
                streakData = streakData,
                onDismiss = { shareDialogHabit = null },
                onShare = {
                    coroutineScope.launch {
                        try {
                            // Render the streak card to a bitmap
                            val bitmap = composableToBitmap(
                                width = 1080,
                                height = 566
                            ) {
                                ShareStreakCard(streakData = streakData)
                            }

                            if (bitmap != null) {
                                // Share the bitmap
                                val shareImage = ShareImage()
                                val success = shareImage.shareImage(bitmap, "My ${streakData.habitTitle} Streak")

                                if (!success) {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Failed to share image",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Failed to create share image",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Error sharing: ${e.message}",
                                duration = SnackbarDuration.Short
                            )
                        } finally {
                            shareDialogHabit = null
                        }
                    }
                }
            )
        }
    }
} 