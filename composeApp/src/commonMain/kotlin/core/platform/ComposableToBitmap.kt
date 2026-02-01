package core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform-specific implementation to render a Composable to an ImageBitmap.
 */
expect suspend fun composableToBitmap(
    width: Int,
    height: Int,
    content: @Composable () -> Unit
): ImageBitmap?
