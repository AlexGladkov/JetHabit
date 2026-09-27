package feature.share.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.share.domain.GetTopHabitsForSharingUseCase
import feature.share.presentation.models.ShareAction
import feature.share.presentation.models.ShareEvent
import feature.share.presentation.models.ShareViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the share feature.
 * Manages the state of share card preparation and preview display.
 */
class ShareViewModel : BaseViewModel<ShareViewState, ShareAction, ShareEvent>(
    initialState = ShareViewState()
) {
    private val getTopHabitsForSharingUseCase = Inject.instance<GetTopHabitsForSharingUseCase>()

    override fun obtainEvent(event: ShareEvent) {
        when (event) {
            ShareEvent.TapShare -> prepareShareCard()
            ShareEvent.ConfirmShare -> shareCard()
            ShareEvent.Dismiss -> dismissPreview()
        }
    }

    /**
     * Prepares the share card data by fetching top habits.
     * Shows the preview screen when data is ready.
     */
    private fun prepareShareCard() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(isLoading = true, error = null)
            }

            try {
                val shareCardData = getTopHabitsForSharingUseCase.execute()

                withContext(Dispatchers.Main) {
                    if (shareCardData.habits.isEmpty()) {
                        viewState = viewState.copy(
                            isLoading = false,
                            error = "No habits to share yet. Start tracking habits to share your achievements!"
                        )
                        viewAction = ShareAction.ShowError("No habits to share yet. Start tracking habits to share your achievements!")
                    } else {
                        viewState = viewState.copy(
                            isLoading = false,
                            shareCardData = shareCardData,
                            isPreviewVisible = true,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        isLoading = false,
                        error = "Failed to prepare share card: ${e.message}"
                    )
                    viewAction = ShareAction.ShowError("Failed to prepare share card. Please try again.")
                }
            }
        }
    }

    /**
     * Triggers the platform-specific share action.
     * Note: The actual image rendering will be done in the UI layer,
     * and then passed back via the share service.
     */
    private fun shareCard() {
        // The actual sharing will be triggered from the UI layer
        // after the composable is rendered to a bitmap.
        // This is handled in SharePreviewScreen.
        dismissPreview()
    }

    /**
     * Dismisses the preview screen.
     */
    private fun dismissPreview() {
        viewState = viewState.copy(isPreviewVisible = false)
    }

    /**
     * Opens the share sheet with the rendered image bytes.
     * Called from the UI layer after rendering the composable to a bitmap.
     */
    fun openShareSheet(imageBytes: ByteArray) {
        viewAction = ShareAction.OpenShareSheet(imageBytes)
    }
}
