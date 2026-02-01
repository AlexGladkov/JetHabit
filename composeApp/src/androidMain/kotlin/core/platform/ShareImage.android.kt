package core.platform

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import di.Inject
import di.PlatformConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual class ShareImage {
    private val platformConfig: PlatformConfiguration = Inject.instance()

    actual suspend fun shareImage(bitmap: ImageBitmap, title: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val context = platformConfig.application
                val activity = platformConfig.activity

                // Convert ImageBitmap to Android Bitmap
                val androidBitmap = bitmap.asAndroidBitmap()

                // Save bitmap to cache directory
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()

                val file = File(cachePath, "streak_${System.currentTimeMillis()}.png")
                FileOutputStream(file).use { out ->
                    androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                // Get URI using FileProvider
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                // Create share intent
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    type = "image/png"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Launch share sheet
                val chooserIntent = Intent.createChooser(shareIntent, "Share Streak")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(chooserIntent)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
