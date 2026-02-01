package core.platform

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform-specific image sharing functionality.
 * Each platform implements sharing via its native share sheet/dialog.
 */
expect class ShareImage() {
    /**
     * Share an image bitmap via the platform's native sharing mechanism.
     *
     * @param bitmap The image to share
     * @param title The title/subject for the share (used in some platforms)
     * @return true if sharing was initiated successfully, false otherwise
     */
    suspend fun shareImage(bitmap: ImageBitmap, title: String): Boolean
}
