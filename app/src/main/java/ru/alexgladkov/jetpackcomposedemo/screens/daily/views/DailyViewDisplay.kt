package ru.alexgladkov.jetpackcomposedemo.screens.daily.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.daily.models.DailyViewState
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

@ExperimentalFoundationApi
@Composable
fun DailyViewDisplay(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewState: DailyViewState.Display,
    onPreviousDayClicked: () -> Unit,
    onNextDayClicked: () -> Unit,
    onCheckedChange: (Long, Boolean) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Box {
            LazyColumn {
                stickyHeader {
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
                                text = viewState.title,
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
                                    .clickable { onPreviousDayClicked.invoke() },
                                text = stringResource(id = R.string.daily_previous_day),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.controlColor
                            )
                        }

                        if (viewState.hasNextDay) {
                            Icon(
                                modifier = Modifier
                                    .size(56.dp)
                                    .padding(16.dp)
                                    .clickable { onNextDayClicked.invoke() },
                                imageVector = Icons.Filled.ArrowForward,
                                tint = JetHabitTheme.colors.controlColor,
                                contentDescription = "Next Day"
                            )
                        }
                    }
                }

                viewState.items
                    .forEach { cardItem ->
                        item {
                            HabitCardItem(
                                model = cardItem,
                                onCheckedChange = { onCheckedChange(cardItem.habitId, !cardItem.isChecked) }
                            )
                        }
                    }
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(JetHabitTheme.shapes.padding),
                backgroundColor = JetHabitTheme.colors.tintColor,
                onClick = {
                    navController.navigate("compose")
                }) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Settings icon",
                    tint = Color.White
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun DailyViewDisplay_Preview() {
    MainTheme {
        DailyViewDisplay(
            navController = rememberNavController(),
            viewState = DailyViewState.Display(
                items = listOf(
                    HabitCardItemModel(
                        habitId = 0,
                        title = "Test habit",
                        isChecked = true
                    ),
                    HabitCardItemModel(
                        habitId = 1,
                        title = "Test habit 2",
                        isChecked = false
                    )
                ),
                title = "Today",
                hasNextDay = true
            ),
            onPreviousDayClicked = {},
            onNextDayClicked = {},
            onCheckedChange = { _, _ -> }
        )
    }
}