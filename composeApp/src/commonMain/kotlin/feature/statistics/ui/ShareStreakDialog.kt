package feature.statistics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import feature.statistics.presentation.models.StreakData

/**
 * Preview dialog displaying the streak card before sharing.
 * Shows the rendered card with "Share" and "Cancel" actions.
 *
 * @param streakData The streak information to display
 * @param onDismiss Callback when the dialog is dismissed or cancelled
 * @param onShare Callback when the user confirms sharing
 */
@Composable
fun ShareStreakDialog(
    streakData: StreakData,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Share Streak")
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Display the share card at a smaller preview size
                ShareStreakCard(
                    streakData = streakData,
                    modifier = Modifier
                        .width(360.dp)
                        .height(189.dp) // Maintains 1.91:1 ratio
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onShare) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}
