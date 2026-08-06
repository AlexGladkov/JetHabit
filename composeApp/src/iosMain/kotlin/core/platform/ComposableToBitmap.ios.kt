package core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual suspend fun composableToBitmap(
    width: Int,
    height: Int,
    content: @Composable () -> Unit
): ImageBitmap? {
    // TODO: Implement iOS-specific composable-to-bitmap rendering
    // This is complex on iOS and may require using UIGraphicsImageRenderer
    // For now, return null and this will need platform-specific rendering
    return null
}
