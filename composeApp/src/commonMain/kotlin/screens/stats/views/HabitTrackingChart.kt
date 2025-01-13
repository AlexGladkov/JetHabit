package screens.stats.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import screens.stats.models.TrackedDay
import ui.themes.JetHabitTheme

@Composable
fun HabitTrackingChart(
    trackedDays: List<TrackedDay>,
    completionRate: Float,
    modifier: Modifier = Modifier
) {
    val tintColor = JetHabitTheme.colors.tintColor
    val secondaryBackground = JetHabitTheme.colors.secondaryBackground
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Completion rate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Completion Rate",
                style = JetHabitTheme.typography.body,
                color = JetHabitTheme.colors.primaryText
            )
            Text(
                text = "${(completionRate * 100).toInt()}%",
                style = JetHabitTheme.typography.body,
                color = JetHabitTheme.colors.tintColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time distance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Time Distance",
                style = JetHabitTheme.typography.caption,
                color = JetHabitTheme.colors.secondaryText
            )
            Text(
                text = "${trackedDays.size} days",
                style = JetHabitTheme.typography.caption,
                color = JetHabitTheme.colors.secondaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tracking chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            val squareSize = (size.width - (trackedDays.size - 1) * 4f) / trackedDays.size
            val spacing = 4f

            trackedDays.forEachIndexed { index, day ->
                val x = index * (squareSize + spacing)
                drawRect(
                    color = if (day.isChecked) tintColor else secondaryBackground,
                    topLeft = Offset(x, 0f),
                    size = Size(squareSize, squareSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = trackedDays.firstOrNull()?.date?.take(10) ?: "",
                style = JetHabitTheme.typography.caption,
                color = JetHabitTheme.colors.secondaryText
            )
            Text(
                text = trackedDays.lastOrNull()?.date?.take(10) ?: "",
                style = JetHabitTheme.typography.caption,
                color = JetHabitTheme.colors.secondaryText
            )
        }
    }
} 