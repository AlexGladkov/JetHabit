package core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

actual suspend fun composableToBitmap(
    width: Int,
    height: Int,
    content: @Composable () -> Unit
): ImageBitmap? {
    // TODO: Implement macOS-specific composable-to-bitmap rendering
    // For now, return null
    return null
}
