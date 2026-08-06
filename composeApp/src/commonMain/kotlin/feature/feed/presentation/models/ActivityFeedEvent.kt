package feature.feed.presentation.models

sealed class ActivityFeedEvent {
    data object LoadFeed : ActivityFeedEvent()
    data object RefreshFeed : ActivityFeedEvent()
    class FeedItemClicked(val habitId: String) : ActivityFeedEvent()
}
