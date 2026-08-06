package feature.share.presentation.models

import androidx.compose.runtime.Stable
import feature.share.domain.models.ShareCardData

/**
 * View state for the share feature.
 */
@Stable
data class ShareViewState(
    val isLoading: Boolean = false,
    val shareCardData: ShareCardData? = null,
    val error: String? = null,
    val isPreviewVisible: Boolean = false
)
