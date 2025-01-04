package feature.health.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import feature.health.presentation.models.TrackerHistoryItem
import org.jetbrains.compose.resources.stringResource
import ui.themes.JetHabitTheme

@Composable
fun TrackerHistoryItem(
    model: TrackerHistoryItem
) {
    Card(
        modifier = Modifier
            .padding(
                horizontal = JetHabitTheme.shapes.padding,
                vertical = JetHabitTheme.shapes.padding / 2
            )
            .fillMaxWidth(),
        elevation = 8.dp,
        backgroundColor = JetHabitTheme.colors.primaryBackground,
        shape = JetHabitTheme.shapes.cornersStyle
    ) {
        Column(
            modifier = Modifier
                .padding(JetHabitTheme.shapes.padding)
                .fillMaxWidth()
        ) {
            Text(
                text = model.title,
                style = JetHabitTheme.typography.body,
                color = JetHabitTheme.colors.primaryText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(model.measurement.stringRes, model.value.toString()),
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )

                Text(
                    text = model.date,
                    style = JetHabitTheme.typography.caption,
                    color = JetHabitTheme.colors.secondaryText,
                    textAlign = TextAlign.End
                )
            }
        }
    }
} 