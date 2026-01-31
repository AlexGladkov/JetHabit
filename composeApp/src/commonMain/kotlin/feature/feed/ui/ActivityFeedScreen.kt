package feature.feed.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import feature.feed.presentation.ActivityFeedViewModel
import feature.feed.presentation.models.ActivityFeedAction
import feature.feed.presentation.models.ActivityFeedEvent
import navigation.DailyScreens
import ui.themes.JetHabitTheme

@Composable
fun ActivityFeedScreen(
    navController: NavController,
    viewModel: ActivityFeedViewModel = viewModel { ActivityFeedViewModel() }
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = JetHabitTheme.shapes.padding,
                        end = JetHabitTheme.shapes.padding,
                        top = JetHabitTheme.shapes.padding + 8.dp,
                        bottom = JetHabitTheme.shapes.padding + 8.dp
                    ),
                text = "Activity Feed",
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText
            )

            // Content
            when {
                viewState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = JetHabitTheme.colors.controlColor
                        )
                    }
                }

                viewState.isEmpty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "No activity yet",
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.secondaryText,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Start checking off habits to see your streak activity!",
                                style = JetHabitTheme.typography.caption,
                                color = JetHabitTheme.colors.secondaryText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = viewState.feedItems,
                            key = { it.id }
                        ) { item ->
                            ActivityFeedItemView(
                                item = item,
                                onItemClicked = {
                                    viewModel.obtainEvent(ActivityFeedEvent.FeedItemClicked(item.habitId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Handle actions
    when (viewAction) {
        is ActivityFeedAction.OpenHabitDetail -> {
            val habitId = (viewAction as ActivityFeedAction.OpenHabitDetail).habitId
            navController.navigate("${DailyScreens.Detail.name}/${habitId}")
            viewModel.clearAction()
        }
        null -> {}
    }

    // Load feed on launch
    LaunchedEffect(Unit) {
        viewModel.obtainEvent(ActivityFeedEvent.LoadFeed)
    }
}
