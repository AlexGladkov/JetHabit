package core.platform

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual class ShareImage {
    actual suspend fun shareImage(bitmap: ImageBitmap, title: String): Boolean {
        // TODO: Implement macOS sharing using NSSharingServicePicker
        // This requires AppKit interop which is complex in Kotlin/Native
        // For now, return false indicating sharing is not implemented
        println("macOS sharing not yet implemented")
        return false
    }
}
