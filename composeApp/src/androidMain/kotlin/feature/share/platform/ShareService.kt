package feature.share.platform

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Android implementation of ShareService.
 * Renders a composable to a bitmap and shares it via Android's share sheet.
 */
actual class ShareService(private val context: Context) {

    /**
     * Renders a composable to a bitmap and shares it via Android's share sheet.
     *
     * @param content The composable content to render
     * @param title The title for the share action
     */
    actual suspend fun shareComposable(content: @Composable () -> Unit, title: String) {
        withContext(Dispatchers.Main) {
            try {
                // Create a ComposeView to render the composable
                val composeView = ComposeView(context).apply {
                    setContent {
                        content()
                    }
                }

                // Measure and layout the view
                val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    (400 * context.resources.displayMetrics.density).toInt(),
                    View.MeasureSpec.EXACTLY
                )
                val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

                composeView.measure(widthSpec, heightSpec)
                composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

                // Create bitmap and draw the view
                val bitmap = Bitmap.createBitmap(
                    composeView.measuredWidth,
                    composeView.measuredHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                composeView.draw(canvas)

                // Save bitmap to cache and share
                withContext(Dispatchers.IO) {
                    shareBitmap(bitmap, title)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Failed to render and share: ${e.message}")
            }
        }
    }

    /**
     * Saves the bitmap to cache and opens the share sheet.
     */
    private fun shareBitmap(bitmap: Bitmap, title: String) {
        try {
            // Save bitmap to cache directory
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()

            val file = File(cachePath, "share_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { fileOutputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.flush()
            }

            // Get URI using FileProvider
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Start chooser
            val chooser = Intent.createChooser(shareIntent, title)
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to share bitmap: ${e.message}")
        }
    }
}
