package feature.daily.ui

import PreviewApp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import feature.daily.ui.models.DailyViewState
import org.jetbrains.compose.ui.tooling.preview.Preview
import feature.daily.ui.models.DailyEvent
import org.jetbrains.compose.resources.stringResource
import feature.daily.ui.views.DailyViewNoItems
import screens.daily.views.HabitCardItem
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.daily_previous_day
import ui.themes.JetHabitTheme
import utils.Weekday
import utils.title

@Composable
fun DailyView(
    viewState: DailyViewState,
    eventHandler: (DailyEvent) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Box {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            modifier = Modifier.padding(
                                start = JetHabitTheme.shapes.padding,
                                end = JetHabitTheme.shapes.padding,
                                top = JetHabitTheme.shapes.padding + 8.dp
                            ),
                            text = viewState.currentDay.title(),
                            style = JetHabitTheme.typography.heading,
                            color = JetHabitTheme.colors.primaryText
                        )

                        Text(
                            modifier = Modifier
                                .padding(
                                    start = JetHabitTheme.shapes.padding,
                                    end = JetHabitTheme.shapes.padding,
                                    top = 4.dp,
                                    bottom = JetHabitTheme.shapes.padding + 8.dp
                                )
                                .clickable { eventHandler.invoke(DailyEvent.PreviousDayClicked) },
                            text = stringResource(Res.string.daily_previous_day),
                            style = JetHabitTheme.typography.body,
                            color = JetHabitTheme.colors.controlColor
                        )
                    }

                    if (viewState.hasNextDay) {
                        Icon(
                            modifier = Modifier
                                .size(56.dp)
                                .padding(16.dp)
                                .clickable { eventHandler.invoke(DailyEvent.NextDayClicked) },
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            tint = JetHabitTheme.colors.controlColor,
                            contentDescription = "Next Day"
                        )
                    }
                }

                if (viewState.habits.isEmpty()) {
                    DailyViewNoItems {
                        eventHandler.invoke(DailyEvent.ComposeAction)
                    }
                } else {
                    LazyColumn {
                        viewState.habits
                            .forEach { cardItem ->
                                item {
                                    HabitCardItem(
                                        model = cardItem,
                                        onCheckedChange = {
                                            eventHandler.invoke(DailyEvent.HabitCheckClicked(cardItem.habitId, it))
                                        },
                                        onCardClicked = {
                                            eventHandler.invoke(DailyEvent.HabitClicked(cardItem.habitId))
                                        }
                                    )
                                }
                            }
                    }
                }
            }

            if (viewState.habits.isNotEmpty()) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(JetHabitTheme.shapes.padding),
                    backgroundColor = JetHabitTheme.colors.tintColor,
                    onClick = {
                        eventHandler.invoke(DailyEvent.ComposeAction)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "Settings icon",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun DailyView_Preview() {
    PreviewApp {
        DailyView(
            viewState = DailyViewState(
                currentDay = Weekday.Today,
                hasNextDay = true,
                habits = emptyList()
            )
        ) {

        }
    }
}