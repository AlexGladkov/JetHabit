package feature.share.presentation.models

/**
 * Actions (side effects) triggered by the ShareViewModel.
 * These represent navigation or platform-specific operations.
 */
sealed interface ShareAction {
    /**
     * Open the native share sheet with the rendered image.
     */
    data class OpenShareSheet(val imageBytes: ByteArray) : ShareAction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as OpenShareSheet

            if (!imageBytes.contentEquals(other.imageBytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return imageBytes.contentHashCode()
        }
    }

    /**
     * Show an error message to the user.
     */
    data class ShowError(val message: String) : ShareAction
}
