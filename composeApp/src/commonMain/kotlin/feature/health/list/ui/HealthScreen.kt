package feature.health.list.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import feature.health.list.presentation.HealthViewModel
import feature.health.list.presentation.models.HealthEvent
import feature.health.list.presentation.models.TrackerHabitItem
import navigation.HealthScreens
import ui.themes.JetHabitTheme

@Composable
fun HealthScreen(
    navController: NavController
) {
    val viewModel = remember { HealthViewModel() }
    val viewState by viewModel.viewStates().collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(JetHabitTheme.shapes.padding)
        ) {
            Text(
                text = "Health Tracking",
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewState.habits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { navController.navigate("${HealthScreens.Create.name}?type=tracker") }
                    ) {
                        Text("Track a Habit")
                    }
                }
            } else {
                LazyColumn {
                    items(viewState.habits) { habit ->
                        TrackerHabitCard(
                            habit = habit,
                            onTrackClick = { 
                                navController.navigate("${HealthScreens.Track.name}/${habit.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackerHabitCard(
    habit: TrackerHabitItem,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onTrackClick),
        backgroundColor = JetHabitTheme.colors.secondaryBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = habit.title,
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )
                    Text(
                        text = habit.lastValue?.let { "${it.toInt()} ${habit.measurement.toString().lowercase().replaceFirstChar { it.uppercase() }}" } ?: "No tracked data",
                        style = JetHabitTheme.typography.caption,
                        color = JetHabitTheme.colors.secondaryText
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = JetHabitTheme.colors.primaryText
                )
            }

            if (habit.values.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                TrackerGraph(
                    values = habit.values,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }
    }
}

@Composable
private fun TrackerGraph(
    values: List<Double>,
    modifier: Modifier = Modifier
) {
    if (values.isEmpty()) return
    val tintColor = JetHabitTheme.colors.tintColor

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxValue = values.maxOrNull() ?: 0.0
        val minValue = values.minOrNull() ?: 0.0
        val range = (maxValue - minValue).coerceAtLeast(0.1)
        val path = Path()
        val strokePath = Path()

        val xStep = width / (values.size - 1).coerceAtLeast(1)
        val points = values.mapIndexed { index, value ->
            Offset(
                x = index * xStep,
                y = height - (height * (value - minValue) / range).toFloat()
            )
        }

        // Draw line graph
        points.forEachIndexed { index, point ->
            if (index == 0) {
                path.moveTo(point.x, point.y)
                strokePath.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
                strokePath.lineTo(point.x, point.y)
            }
        }

        // Draw the line
        drawPath(
            path = strokePath,
            color = tintColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = tintColor,
                radius = 3.dp.toPx(),
                center = point
            )
        }
    }
} 