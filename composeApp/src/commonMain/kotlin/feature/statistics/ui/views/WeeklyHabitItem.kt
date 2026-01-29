package feature.statistics.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.statistics.ui.models.WeeklyHabitStat
import ui.themes.JetHabitTheme
import kotlin.math.roundToInt

@Composable
fun WeeklyHabitItem(
    habitStat: WeeklyHabitStat,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val completedDays = habitStat.dailyStatus.count { it.isChecked && it.isApplicable }
    val totalDays = habitStat.dailyStatus.count { it.isApplicable }
    val completionRate = if (totalDays > 0) {
        (completedDays.toFloat() / totalDays) * 100
    } else {
        0f
    }

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
                    text = habitStat.habitTitle,
                    style = JetHabitTheme.typography.toolbar,
                    color = JetHabitTheme.colors.primaryText,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${completionRate.roundToInt()}%",
                    style = JetHabitTheme.typography.toolbar,
                    color = JetHabitTheme.colors.tintColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                habitStat.dailyStatus.forEachIndexed { index, dayStatus ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = dayLabels[index],
                            style = JetHabitTheme.typography.body,
                            color = JetHabitTheme.colors.secondaryText
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = when {
                                        !dayStatus.isApplicable -> JetHabitTheme.colors.errorColor.copy(alpha = 0.05f)
                                        dayStatus.isChecked -> JetHabitTheme.colors.tintColor
                                        else -> JetHabitTheme.colors.errorColor.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}
