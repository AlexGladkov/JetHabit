package feature.statistics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.LocalPlatform
import di.Platform
import feature.share.presentation.ShareViewModel
import feature.share.presentation.models.ShareEvent
import feature.share.ui.SharePreviewScreen
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

    val shareViewModel = remember { ShareViewModel() }
    val shareViewState by shareViewModel.viewStates().collectAsState()

    val platform = LocalPlatform.current

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(StatisticsEvent.LoadStatistics)
    }

    // Show share preview dialog when data is ready
    if (shareViewState.isPreviewVisible && shareViewState.shareCardData != null) {
        SharePreviewScreen(
            shareCardData = shareViewState.shareCardData!!,
            onDismiss = { shareViewModel.obtainEvent(ShareEvent.Dismiss) },
            onShared = { shareViewModel.obtainEvent(ShareEvent.Dismiss) }
        )
    }

    Scaffold(
        backgroundColor = JetHabitTheme.colors.primaryBackground,
        floatingActionButton = {
            // Only show share button on Android and iOS platforms
            if (platform == Platform.Android || platform == Platform.iOS) {
                if (viewState.hasData && !viewState.isLoading) {
                    FloatingActionButton(
                        onClick = { shareViewModel.obtainEvent(ShareEvent.TapShare) },
                        backgroundColor = JetHabitTheme.colors.tintColor
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share achievements",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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