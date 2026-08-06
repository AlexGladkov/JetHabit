package feature.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import feature.feed.data.ActivityFeedEntity
import feature.feed.domain.model.ActivityFeedType
import ui.themes.JetHabitTheme

@Composable
fun ActivityFeedItemView(
    item: ActivityFeedEntity,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = JetHabitTheme.shapes.padding,
                vertical = 8.dp
            )
            .clickable { onItemClicked() },
        shape = RoundedCornerShape(JetHabitTheme.shapes.cornersStyle),
        backgroundColor = JetHabitTheme.colors.primaryBackground,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon indicating type
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when (item.type) {
                            ActivityFeedType.STREAK_INCREMENT -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                            ActivityFeedType.STREAK_BROKEN -> Color(0xFFF44336).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.type) {
                        ActivityFeedType.STREAK_INCREMENT -> Icons.Default.Favorite
                        ActivityFeedType.STREAK_BROKEN -> Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = when (item.type) {
                        ActivityFeedType.STREAK_INCREMENT -> Color(0xFF4CAF50)
                        ActivityFeedType.STREAK_BROKEN -> Color(0xFFF44336)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.habitTitle,
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.primaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (item.type) {
                        ActivityFeedType.STREAK_INCREMENT -> "${item.streakCount} day streak!"
                        ActivityFeedType.STREAK_BROKEN -> "Streak broken (was ${item.streakCount} days)"
                    },
                    style = JetHabitTheme.typography.caption,
                    color = when (item.type) {
                        ActivityFeedType.STREAK_INCREMENT -> Color(0xFF4CAF50)
                        ActivityFeedType.STREAK_BROKEN -> Color(0xFFF44336)
                    }
                )
            }
        }
    }
}
