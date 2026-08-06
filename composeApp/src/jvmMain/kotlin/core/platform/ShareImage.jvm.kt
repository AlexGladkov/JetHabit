package core.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

actual class ShareImage {
    actual suspend fun shareImage(bitmap: ImageBitmap, title: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Convert ImageBitmap to BufferedImage
                val bufferedImage = bitmap.toAwtImage()

                // Show save dialog
                val fileDialog = FileDialog(null as Frame?, "Save Streak Image", FileDialog.SAVE).apply {
                    file = "streak_${System.currentTimeMillis()}.png"
                    isVisible = true
                }

                val directory = fileDialog.directory
                val filename = fileDialog.file

                if (directory != null && filename != null) {
                    val file = File(directory, filename)
                    ImageIO.write(bufferedImage, "png", file)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
