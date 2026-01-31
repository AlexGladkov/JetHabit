package core.platform

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

actual class ShareService {
    actual suspend fun shareImage(imageBytes: ByteArray, text: String) {
        try {
            if (imageBytes.isEmpty()) {
                // No image to share, just copy text to clipboard
                copyTextToClipboard(text)
                println("Streak text copied to clipboard!")
                println(text)
                return
            }

            // Convert bytes to BufferedImage
            val inputStream = ByteArrayInputStream(imageBytes)
            val image = ImageIO.read(inputStream)

            if (image == null) {
                // Fallback to text if image reading fails
                copyTextToClipboard(text)
                println("Failed to read image. Streak text copied to clipboard instead!")
                println(text)
                return
            }

            // Copy image to clipboard
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val transferableImage = TransferableImage(image)
            clipboard.setContents(transferableImage, null)

            println("Streak image copied to clipboard!")
            println(text)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to text on error
            try {
                copyTextToClipboard(text)
                println("Error occurred. Streak text copied to clipboard instead!")
                println(text)
            } catch (fallbackError: Exception) {
                fallbackError.printStackTrace()
            }
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val transferable = java.awt.datatransfer.StringSelection(text)
        clipboard.setContents(transferable, null)
    }

    private class TransferableImage(private val image: BufferedImage) : Transferable {
        override fun getTransferDataFlavors(): Array<DataFlavor> {
            return arrayOf(DataFlavor.imageFlavor)
        }

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            return DataFlavor.imageFlavor.equals(flavor)
        }

        override fun getTransferData(flavor: DataFlavor): Any {
            if (isDataFlavorSupported(flavor)) {
                return image
            }
            throw UnsupportedOperationException("Flavor not supported")
        }
    }
}
