package core.platform

import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import di.Inject
import di.PlatformConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

actual suspend fun composableToBitmap(
    width: Int,
    height: Int,
    content: @Composable () -> Unit
): ImageBitmap? = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { continuation ->
        try {
            val platformConfig: PlatformConfiguration = Inject.instance()
            val context = platformConfig.application

            // Create a ComposeView to render the composable
            val composeView = ComposeView(context).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent {
                    content()
                }
            }

            // Measure and layout the view
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            composeView.measure(widthSpec, heightSpec)
            composeView.layout(0, 0, width, height)

            // Draw the view to a bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            composeView.draw(canvas)

            continuation.resume(bitmap.asImageBitmap())
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(null)
        }
    }
}
