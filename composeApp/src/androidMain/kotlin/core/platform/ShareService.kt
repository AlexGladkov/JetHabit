package core.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

actual class ShareService(private val context: Context) {
    actual suspend fun shareImage(imageBytes: ByteArray, text: String) {
        try {
            // Save image to cache directory
            val cacheDir = File(context.cacheDir, "share_images")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val imageFile = File(cacheDir, "streak_${System.currentTimeMillis()}.png")
            FileOutputStream(imageFile).use { output ->
                output.write(imageBytes)
            }

            // Get URI for the file using FileProvider
            val imageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share your streak")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
