package feature.share.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import di.Inject
import feature.share.domain.models.ShareCardData
import feature.share.platform.ShareService
import kotlinx.coroutines.launch
import ui.themes.JetHabitTheme

/**
 * Preview screen that displays the share card before sharing.
 * Shows a dialog with the rendered card and action buttons.
 */
@Composable
fun SharePreviewScreen(
    shareCardData: ShareCardData,
    onDismiss: () -> Unit,
    onShared: () -> Unit
) {
    val shareService = Inject.instance<ShareService>()
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            color = JetHabitTheme.colors.primaryBackground,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preview",
                        style = JetHabitTheme.typography.heading,
                        color = JetHabitTheme.colors.primaryText
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = JetHabitTheme.colors.controlColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content with the share card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    ShareCardContent(shareCardData = shareCardData)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                shareService.shareComposable(
                                    content = { ShareCardContent(shareCardData = shareCardData) },
                                    title = "My Habit Achievements"
                                )
                                onShared()
                            } catch (e: Exception) {
                                // Handle error - could show a toast or snackbar
                                println("Error sharing: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = JetHabitTheme.colors.tintColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share",
                        style = JetHabitTheme.typography.body,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }
}
