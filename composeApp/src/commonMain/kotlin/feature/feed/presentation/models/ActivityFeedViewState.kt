package feature.feed.presentation.models

import feature.feed.data.ActivityFeedEntity

data class ActivityFeedViewState(
    val feedItems: List<ActivityFeedEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
