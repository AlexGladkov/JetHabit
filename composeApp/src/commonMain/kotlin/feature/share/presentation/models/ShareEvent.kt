package feature.share.presentation.models

/**
 * Events triggered by user interactions in the share feature.
 */
sealed interface ShareEvent {
    /**
     * User tapped the "Share" button on the Statistics screen.
     * This should prepare the share card data and show the preview.
     */
    data object TapShare : ShareEvent

    /**
     * User confirmed sharing from the preview screen.
     * This should trigger the native share sheet.
     */
    data object ConfirmShare : ShareEvent

    /**
     * User dismissed the preview screen without sharing.
     */
    data object Dismiss : ShareEvent
}
