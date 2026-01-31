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
            // Convert bytes to BufferedImage
            val inputStream = ByteArrayInputStream(imageBytes)
            val image = ImageIO.read(inputStream)

            // Copy image to clipboard
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val transferableImage = TransferableImage(image)
            clipboard.setContents(transferableImage, null)

            println("Streak image copied to clipboard!")
            println(text)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
