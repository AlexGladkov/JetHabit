package feature.statistics.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import ui.themes.JetHabitTheme

@Composable
fun WeeklyBarChart(
    dailyCompletionCounts: List<Int>,
    dailyTotalCounts: List<Int>,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxValue = dailyTotalCounts.maxOrNull() ?: 1
                val barWidth = (size.width - (8 * 8.dp.toPx())) / 7f // 8dp spacing between bars
                val chartHeight = size.height - 20.dp.toPx() // Leave space for labels

                dailyCompletionCounts.forEachIndexed { index, completedCount ->
                    val totalCount = dailyTotalCounts[index]
                    val x = index * (barWidth + 8.dp.toPx()) + 4.dp.toPx()

                    // Background bar (total capacity)
                    val backgroundHeight = if (maxValue > 0) {
                        (totalCount.toFloat() / maxValue) * chartHeight
                    } else {
                        0f
                    }

                    drawRoundedBar(
                        x = x,
                        y = chartHeight - backgroundHeight,
                        width = barWidth,
                        height = backgroundHeight,
                        color = JetHabitTheme.colors.controlColor
                    )

                    // Foreground bar (completed)
                    val foregroundHeight = if (maxValue > 0) {
                        (completedCount.toFloat() / maxValue) * chartHeight
                    } else {
                        0f
                    }

                    drawRoundedBar(
                        x = x,
                        y = chartHeight - foregroundHeight,
                        width = barWidth,
                        height = foregroundHeight,
                        color = JetHabitTheme.colors.tintColor
                    )
                }
            }
        }

        // Day labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dayLabels.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawRoundedBar(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: androidx.compose.ui.graphics.Color
) {
    if (height > 0) {
        val cornerRadius = 4.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(x, y),
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }
}
