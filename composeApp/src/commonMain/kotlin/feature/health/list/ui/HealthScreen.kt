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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import feature.health.list.presentation.HealthViewModel
import feature.health.list.presentation.models.TrackerHabitItem
import feature.health.list.ui.views.HealthViewNoItems
import navigation.HealthScreens
import ui.themes.JetHabitTheme
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.health_title
import kotlinx.datetime.LocalDate

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
            if (viewState.habits.isEmpty()) {
                HealthViewNoItems(
                    onTrackClick = { navController.navigate("${HealthScreens.Create.name}?type=tracker") }
                )
            } else {
                Text(
                    text = stringResource(Res.string.health_title),
                    style = JetHabitTheme.typography.heading,
                    color = JetHabitTheme.colors.primaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    values = habit.values.reversed(),
                    dates = habit.dates.reversed(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun TrackerGraph(
    values: List<Double>,
    dates: List<String>,
    modifier: Modifier = Modifier
) {
    if (values.isEmpty()) return
    val tintColor = JetHabitTheme.colors.tintColor
    val textColor = JetHabitTheme.colors.secondaryText
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontSize = 10.sp,
        color = textColor
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val leftPadding = 35f // Space for y-axis labels
        val bottomPadding = 45f // More space for x-axis labels
        val topPadding = 10f // Space from top
        val rightPadding = 10f // Space from right
        
        val graphWidth = width - (leftPadding + rightPadding)
        val graphHeight = height - (bottomPadding + topPadding)

        val maxValue = values.maxOrNull() ?: 0.0
        val minValue = values.minOrNull() ?: 0.0
        val range = (maxValue - minValue).coerceAtLeast(0.1)

        // Draw axes
        drawLine(
            color = textColor,
            start = Offset(leftPadding, topPadding),
            end = Offset(leftPadding, height - bottomPadding),
            strokeWidth = 1f
        )
        drawLine(
            color = textColor,
            start = Offset(leftPadding, height - bottomPadding),
            end = Offset(width - rightPadding, height - bottomPadding),
            strokeWidth = 1f
        )

        // Draw min and max y-axis labels
        drawText(
            textMeasurer = textMeasurer,
            text = String.format("%.0f", maxValue),
            style = textStyle,
            topLeft = Offset(0f, topPadding)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = String.format("%.0f", minValue),
            style = textStyle,
            topLeft = Offset(0f, height - bottomPadding - 8f)
        )

        // Draw start and end dates
        if (dates.size >= 2) {
            // Format dates as dd/mm
            val formatDate = { date: String ->
                val localDate = LocalDate.parse(date)
                "${localDate.dayOfMonth.toString().padStart(2, '0')}/${localDate.monthNumber.toString().padStart(2, '0')}"
            }

            // Start date
            val startDate = formatDate(dates.first())
            drawText(
                textMeasurer = textMeasurer,
                text = startDate,
                style = textStyle,
                topLeft = Offset(leftPadding - 10f, height - bottomPadding + 15f)
            )

            // End date
            val endDate = formatDate(dates.last())
            val endDateResult = textMeasurer.measure(endDate, textStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = endDate,
                style = textStyle,
                topLeft = Offset(width - rightPadding - endDateResult.size.width + 10f, height - bottomPadding + 15f)
            )
        }

        // Draw line graph
        val xStep = graphWidth / (values.size - 1).coerceAtLeast(1)
        val points = values.mapIndexed { index, value ->
            Offset(
                x = leftPadding + (index * xStep),
                y = topPadding + (graphHeight * (1 - (value - minValue) / range)).toFloat()
            )
        }

        // Draw the line
        val path = Path()
        points.forEachIndexed { index, point ->
            if (index == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }

        drawPath(
            path = path,
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