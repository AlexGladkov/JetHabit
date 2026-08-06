package feature.share.platform

import androidx.compose.runtime.Composable

/**
 * JVM/Desktop implementation of ShareService.
 * No-op implementation as sharing is not supported on Desktop platforms per requirements.
 */
actual class ShareService {

    /**
     * Sharing is not supported on Desktop/JVM platforms.
     * This method throws UnsupportedOperationException.
     */
    actual suspend fun shareComposable(content: @Composable () -> Unit, title: String) {
        throw UnsupportedOperationException(
            "Sharing is not supported on Desktop platforms. " +
            "This feature is only available on Android and iOS."
        )
    }
}
