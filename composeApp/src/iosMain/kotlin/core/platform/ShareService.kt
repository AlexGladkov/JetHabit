package core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
actual class ShareService {
    private var currentViewController: UIViewController? = null

    fun setViewController(viewController: UIViewController) {
        currentViewController = viewController
    }

    actual suspend fun shareImage(imageBytes: ByteArray, text: String) {
        val nsData = imageBytes.usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = imageBytes.size.toULong()
            )
        }

        val itemsToShare = listOf(text, nsData)

        val activityViewController = UIActivityViewController(
            activityItems = itemsToShare,
            applicationActivities = null
        )

        currentViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}
