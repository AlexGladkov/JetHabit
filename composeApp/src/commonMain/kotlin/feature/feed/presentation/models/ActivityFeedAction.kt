package feature.feed.presentation.models

sealed class ActivityFeedAction {
    class OpenHabitDetail(val habitId: String) : ActivityFeedAction()
}
