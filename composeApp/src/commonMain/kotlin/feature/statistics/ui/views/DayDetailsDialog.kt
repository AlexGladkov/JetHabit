package feature.statistics.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import feature.daily.domain.HeatmapDayDetails
import navigation.AppTestTags
import ui.themes.JetHabitTheme

/**
 * E7: day details dialog shown on heatmap cell click.
 * Plain dialog — no new navigation destination.
 */
@Composable
internal fun DayDetailsDialog(
    details: HeatmapDayDetails,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.testTag(AppTestTags.DayDetailsDialog),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = details.date.toString(),
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText
            )
        },
        text = {
            if (details.entries.isEmpty()) {
                Text(
                    text = "Статистики пока нет",
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    details.entries.forEach { entry ->
                        Text(
                            text = "${if (entry.isChecked) "✓" else "✗"} ${entry.habitTitle}",
                            style = JetHabitTheme.typography.body,
                            color = JetHabitTheme.colors.primaryText
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag(AppTestTags.DayDetailsDismiss)
            ) {
                Text(text = "Закрыть", color = JetHabitTheme.colors.tintColor)
            }
        }
    )
}
