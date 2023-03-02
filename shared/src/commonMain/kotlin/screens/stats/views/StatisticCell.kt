package screens.stats.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.themes.JetHabitTheme

data class StatisticCellModel(
    val title: String,
    val activeDayList: List<Boolean>,
    val isPeriodic: Boolean
)

@Composable
fun StatisticCell(model: StatisticCellModel) {
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
        Column {
            val activeDays = model.activeDayList.count { it }
            val totalDays = model.activeDayList.size
            val percentage = activeDays.toFloat() / totalDays.toFloat()

            Divider(
                modifier = Modifier.fillMaxWidth(percentage).height(2.dp)
                    .background(JetHabitTheme.colors.tintColor)
            )

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f).padding(top = 8.dp),
                    text = model.title,
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.primaryText
                )

                val countText = if (!model.isPeriodic) activeDays.toString() else
                    "$activeDays / ${model.activeDayList.size}"

                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = countText,
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
            }
        }
    }
}