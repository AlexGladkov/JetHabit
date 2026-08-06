package feature.share.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import feature.share.domain.models.ShareCardData

/**
 * Composable that renders the branded share card.
 * This composable is rendered to a bitmap for sharing.
 *
 * The card has a fixed branded design (not theme-dependent) with:
 * - Dark gradient background
 * - White text
 * - Top 5 habits with streak and completion rate
 * - "+N more habits" note if applicable
 * - "JetHabit" branding at the bottom
 */
@Composable
fun ShareCardContent(shareCardData: ShareCardData, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(400.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1F2E),  // Dark blue-gray top
                        Color(0xFF23282D)   // Dark gray bottom
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = "My Habit Achievements",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Habits list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                shareCardData.habits.forEachIndexed { index, habit ->
                    HabitRow(
                        title = habit.title,
                        streak = habit.currentStreak,
                        completionRate = habit.completionRate
                    )

                    // Add divider between items (but not after the last item)
                    if (index < shareCardData.habits.size - 1) {
                        Divider(
                            color = Color.White.copy(alpha = 0.1f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            // "+N more habits" note
            if (shareCardData.totalHabitCount > shareCardData.habits.size) {
                val moreCount = shareCardData.totalHabitCount - shareCardData.habits.size
                Text(
                    text = "+$moreCount more habit${if (moreCount > 1) "s" else ""}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // JetHabit branding
            Text(
                text = "JetHabit",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Single habit row showing title, streak, and completion rate.
 */
@Composable
private fun HabitRow(
    title: String,
    streak: Int,
    completionRate: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Habit title
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Streak and completion rate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak with flame emoji
            Text(
                text = "\uD83D\uDD25 $streak day${if (streak != 1) "s" else ""}",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFF9F40)  // Orange flame color
                )
            )

            // Bullet separator
            Text(
                text = "•",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            )

            // Completion rate
            val percentText = "${(completionRate * 100).toInt()}%"
            Text(
                text = percentText,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7EE6C3)  // Green success color
                )
            )
        }
    }
}
