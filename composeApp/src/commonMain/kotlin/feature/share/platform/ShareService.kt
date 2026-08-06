package feature.share.platform

import androidx.compose.runtime.Composable

/**
 * Platform-specific service for sharing images.
 * Provides functionality to render composables to images and share them via the native share sheet.
 */
expect class ShareService {
    /**
     * Renders a composable to a bitmap and shares it via the platform's share sheet.
     *
     * @param content The composable content to render
     * @param title The title for the share action
     */
    suspend fun shareComposable(content: @Composable () -> Unit, title: String)
}
