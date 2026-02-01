package core.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class ShareImage {
    private var currentViewController: UIViewController? = null

    fun setViewController(viewController: UIViewController) {
        currentViewController = viewController
    }

    actual suspend fun shareImage(bitmap: ImageBitmap, title: String): Boolean = suspendCoroutine { continuation ->
        try {
            // Convert ImageBitmap to UIImage
            val uiImage = bitmap.toUIImage()

            if (uiImage == null) {
                continuation.resume(false)
                return@suspendCoroutine
            }

            // Create activity items
            val itemsToShare = listOf(uiImage, title)

            // Create UIActivityViewController
            val activityViewController = UIActivityViewController(
                activityItems = itemsToShare,
                applicationActivities = null
            )

            // Present the share sheet
            currentViewController?.presentViewController(
                activityViewController,
                animated = true,
                completion = {
                    continuation.resume(true)
                }
            ) ?: run {
                continuation.resume(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(false)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ImageBitmap.toUIImage(): UIImage? {
        return try {
            // TODO: This implementation is incomplete and will likely return null
            // Need to properly extract pixel data from ImageBitmap and draw into CGContext
            // Current line below doesn't convert - it just returns the same ImageBitmap type
            val skikoImage = this.toComposeImageBitmap()

            // Create a bitmap context and draw the image
            val width = this.width
            val height = this.height
            val bytesPerPixel = 4
            val bytesPerRow = bytesPerPixel * width
            val bitsPerComponent = 8

            memScoped {
                val colorSpace = CGColorSpaceCreateDeviceRGB()
                val bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value or
                                CGBitmapInfo.kCGBitmapByteOrder32Big.value

                val context = CGBitmapContextCreate(
                    data = null,
                    width = width.toULong(),
                    height = height.toULong(),
                    bitsPerComponent = bitsPerComponent.toULong(),
                    bytesPerRow = bytesPerRow.toULong(),
                    space = colorSpace,
                    bitmapInfo = bitmapInfo
                )

                if (context != null) {
                    // TODO: Need to actually draw the bitmap data into the context here
                    // Currently creates an empty image
                    val cgImage = CGBitmapContextCreateImage(context)
                    if (cgImage != null) {
                        UIImage.imageWithCGImage(cgImage)
                    } else null
                } else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
