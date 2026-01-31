package core.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

actual class ShareService(private val context: Context) {
    actual suspend fun shareImage(imageBytes: ByteArray, text: String) {
        try {
            // If no image bytes, just share text
            if (imageBytes.isEmpty()) {
                shareTextOnly(text)
                return
            }

            // Save image to cache directory
            val cacheDir = File(context.cacheDir, "share_images")
            if (!cacheDir.exists()) {
                if (!cacheDir.mkdirs()) {
                    // Fallback to text sharing if directory creation fails
                    shareTextOnly(text)
                    return
                }
            }

            // Clean up old cached images to prevent excessive storage usage
            cleanOldCacheFiles(cacheDir)

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
            // Fallback to text sharing on error
            try {
                shareTextOnly(text)
            } catch (fallbackError: Exception) {
                fallbackError.printStackTrace()
            }
        }
    }

    private fun shareTextOnly(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share your streak")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    private fun cleanOldCacheFiles(cacheDir: File) {
        try {
            val files = cacheDir.listFiles() ?: return
            val now = System.currentTimeMillis()
            val maxAge = 24 * 60 * 60 * 1000 // 24 hours

            files.forEach { file ->
                if (file.isFile && (now - file.lastModified()) > maxAge) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
            e.printStackTrace()
        }
    }
}
