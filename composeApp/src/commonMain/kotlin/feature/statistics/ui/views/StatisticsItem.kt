package feature.statistics.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import screens.stats.models.TrackedDay
import ui.themes.JetHabitTheme
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate

@Composable
fun StatisticsItem(
    title: String,
    completionRate: Float,
    trackedDays: List<TrackedDay>,
    currentStreak: Int = 0,
    onShareClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = JetHabitTheme.colors.secondaryBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = JetHabitTheme.typography.toolbar,
                    color = JetHabitTheme.colors.primaryText,
                    modifier = Modifier.weight(1f)
                )

                if (currentStreak > 0 && onShareClick != null) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share streak",
                            tint = JetHabitTheme.colors.tintColor
                        )
                    }
                }
            }

            if (currentStreak > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Streak",
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )

                    Text(
                        text = "$currentStreak ${if (currentStreak == 1) "day" else "days"}",
                        style = JetHabitTheme.typography.toolbar,
                        color = JetHabitTheme.colors.tintColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completion Rate",
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
                
                Text(
                    text = "${(completionRate * 100).roundToInt()}%",
                    style = JetHabitTheme.typography.toolbar,
                    color = JetHabitTheme.colors.tintColor
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Time Distance",
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
                
                Text(
                    text = "${trackedDays.size} days",
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    trackedDays.forEach { day ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = when {
                                        day.isChecked -> JetHabitTheme.colors.tintColor
                                        day.wasEverChecked -> JetHabitTheme.colors.errorColor.copy(alpha = 0.2f)
                                        else -> JetHabitTheme.colors.errorColor.copy(alpha = 0.05f)
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (trackedDays.isNotEmpty()) {
                    Text(
                        text = formatDate(trackedDays.first().date),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )
                    Text(
                        text = formatDate(trackedDays.last().date),
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    val date = LocalDate.parse(dateString)
    return "${date.dayOfMonth.toString().padStart(2, '0')}.${date.monthNumber.toString().padStart(2, '0')}.${date.year}"
} 