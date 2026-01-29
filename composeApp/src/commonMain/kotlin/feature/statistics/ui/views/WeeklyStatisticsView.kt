package feature.statistics.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.statistics.ui.models.WeeklyData
import ui.themes.JetHabitTheme
import kotlinx.datetime.LocalDate

@Composable
fun WeeklyStatisticsView(
    weeklyData: WeeklyData?,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (weeklyData == null) {
        StatisticsViewNoItems()
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            // Week navigator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Week",
                    tint = JetHabitTheme.colors.tintColor,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onPreviousWeek() }
                )

                Text(
                    text = formatDateRange(weeklyData.weekStart, weeklyData.weekEnd),
                    style = JetHabitTheme.typography.toolbar,
                    color = JetHabitTheme.colors.primaryText
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Week",
                    tint = JetHabitTheme.colors.tintColor,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onNextWeek() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Progress indicator
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                WeeklyProgressIndicator(completionRate = weeklyData.completionRate)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Completion summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${weeklyData.completedCount} of ${weeklyData.totalCount} habits completed",
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Bar chart
            WeeklyBarChart(
                dailyCompletionCounts = weeklyData.dailyCompletionCounts,
                dailyTotalCounts = weeklyData.dailyTotalCounts,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Habit Breakdown",
                style = JetHabitTheme.typography.toolbar,
                color = JetHabitTheme.colors.primaryText,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(weeklyData.habitStats) { habitStat ->
            WeeklyHabitItem(habitStat = habitStat)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun formatDateRange(start: LocalDate, end: LocalDate): String {
    val startFormatted = "${start.dayOfMonth.toString().padStart(2, '0')}.${start.monthNumber.toString().padStart(2, '0')}"
    val endFormatted = "${end.dayOfMonth.toString().padStart(2, '0')}.${end.monthNumber.toString().padStart(2, '0')}.${end.year}"
    return "$startFormatted – $endFormatted"
}
