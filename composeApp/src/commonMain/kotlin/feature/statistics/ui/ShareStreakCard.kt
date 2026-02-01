package feature.statistics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import feature.statistics.presentation.models.StreakData
import ui.themes.JetHabitTheme

/**
 * Composable card displaying habit streak information for sharing.
 * Card dimensions: ~1080x566px (1.91:1 ratio, optimized for social media).
 *
 * @param streakData The streak information to display
 * @param modifier Optional modifier for the card
 */
@Composable
fun ShareStreakCard(
    streakData: StreakData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 1080.dp, height = 566.dp)
            .background(
                color = JetHabitTheme.colors.primaryBackground,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Habit title
            Text(
                text = streakData.habitTitle,
                style = JetHabitTheme.typography.heading.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = JetHabitTheme.colors.primaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Streak count
            Text(
                text = "${streakData.streakCount}",
                style = JetHabitTheme.typography.heading.copy(
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = JetHabitTheme.colors.tintColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // "days" label
            Text(
                text = if (streakData.streakCount == 1) "day" else "days",
                style = JetHabitTheme.typography.body.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = JetHabitTheme.colors.secondaryText,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Branding
            Text(
                text = "JetHabit",
                style = JetHabitTheme.typography.caption.copy(
                    fontSize = 24.sp
                ),
                color = JetHabitTheme.colors.secondaryText
            )
        }
    }
}
