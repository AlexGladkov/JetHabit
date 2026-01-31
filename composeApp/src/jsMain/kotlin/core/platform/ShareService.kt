package core.platform

import kotlinx.browser.window
import org.khronos.webgl.Uint8Array
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.js.Promise

actual class ShareService {
    actual suspend fun shareImage(imageBytes: ByteArray, text: String) {
        try {
            // Convert ByteArray to Uint8Array
            val uint8Array = Uint8Array(imageBytes.size)
            imageBytes.forEachIndexed { index, byte ->
                uint8Array[index] = byte
            }

            // Create Blob from the byte array
            val blob = Blob(arrayOf(uint8Array), BlobPropertyBag(type = "image/png"))

            // Try using Web Share API if available
            val navigator = window.navigator.asDynamic()
            if (navigator.share != undefined && navigator.canShare != undefined) {
                val shareData = js("{}")
                shareData.text = text
                shareData.files = arrayOf(
                    js("new File([blob], 'streak.png', { type: 'image/png' })").also {
                        it.asDynamic().blob = blob
                    }
                )

                if (navigator.canShare(shareData)) {
                    (navigator.share(shareData) as Promise<Unit>).catch { error ->
                        console.error("Error sharing:", error)
                        fallbackToClipboard(text)
                    }
                    return
                }
            }

            // Fallback: Copy text to clipboard
            fallbackToClipboard(text)
        } catch (e: Exception) {
            console.error("Share failed:", e.message ?: "Unknown error")
            fallbackToClipboard(text)
        }
    }

    private fun fallbackToClipboard(text: String) {
        try {
            val navigator = window.navigator.asDynamic()
            if (navigator.clipboard != undefined) {
                navigator.clipboard.writeText(text).then(
                    { console.log("Text copied to clipboard!") },
                    { error: dynamic -> console.error("Failed to copy:", error) }
                )
            } else {
                console.log("Clipboard not supported. Share text: $text")
            }
        } catch (e: Exception) {
            console.log("Share text: $text")
        }
    }
}
