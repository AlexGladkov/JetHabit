package feature.share.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGImageAlphaInfo
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

/**
 * iOS implementation of ShareService.
 * Renders a composable to a UIImage and shares it via UIActivityViewController.
 */
actual class ShareService {

    /**
     * Renders a composable to a UIImage and shares it via iOS share sheet.
     *
     * @param content The composable content to render
     * @param title The title for the share action
     */
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun shareComposable(content: @Composable () -> Unit, title: String) {
        withContext(Dispatchers.Main) {
            try {
                // Note: Compose Multiplatform on iOS doesn't have direct bitmap rendering yet.
                // This is a simplified implementation that would need proper bitmap capture.
                // For now, we'll throw an error with a clear message about the limitation.

                throw NotImplementedError(
                    "Composable to image rendering on iOS requires additional Skia/Compose setup. " +
                    "This feature is currently under development for iOS platform."
                )

                // Placeholder for future implementation:
                // 1. Use Skia to render the composable to a bitmap
                // 2. Convert Skia bitmap to UIImage
                // 3. Present UIActivityViewController with the image

                // Example of what the sharing code would look like:
                // val activityViewController = UIActivityViewController(
                //     activityItems = listOf(uiImage, title),
                //     applicationActivities = null
                // )
                // val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                // rootViewController?.presentViewController(activityViewController, animated = true, completion = null)

            } catch (e: Exception) {
                throw Exception("Failed to share on iOS: ${e.message}")
            }
        }
    }
}
