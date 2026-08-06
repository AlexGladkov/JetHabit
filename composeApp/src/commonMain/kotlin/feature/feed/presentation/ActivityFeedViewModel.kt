package feature.feed.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.feed.domain.DetectBrokenStreaksUseCase
import feature.feed.domain.GetActivityFeedUseCase
import feature.feed.presentation.models.ActivityFeedAction
import feature.feed.presentation.models.ActivityFeedEvent
import feature.feed.presentation.models.ActivityFeedViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityFeedViewModel : BaseViewModel<ActivityFeedViewState, ActivityFeedAction, ActivityFeedEvent>(
    initialState = ActivityFeedViewState()
) {

    private val getActivityFeedUseCase = Inject.instance<GetActivityFeedUseCase>()
    private val detectBrokenStreaksUseCase = Inject.instance<DetectBrokenStreaksUseCase>()

    init {
        detectBrokenStreaksAndLoadFeed()
        observeFeedUpdates()
    }

    override fun obtainEvent(viewEvent: ActivityFeedEvent) {
        when (viewEvent) {
            ActivityFeedEvent.LoadFeed -> loadFeed()
            ActivityFeedEvent.RefreshFeed -> {
                detectBrokenStreaksAndLoadFeed()
            }
            is ActivityFeedEvent.FeedItemClicked -> {
                viewAction = ActivityFeedAction.OpenHabitDetail(viewEvent.habitId)
            }
        }
    }

    private fun detectBrokenStreaksAndLoadFeed() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(isLoading = true)
                }

                // First detect any broken streaks
                detectBrokenStreaksUseCase.execute()

                // Then load the feed
                loadFeed()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(isLoading = false)
                }
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val feedItems = getActivityFeedUseCase.execute()

                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        feedItems = feedItems,
                        isLoading = false,
                        isEmpty = feedItems.isEmpty()
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        isLoading = false,
                        isEmpty = true
                    )
                }
            }
        }
    }

    private fun observeFeedUpdates() {
        viewModelScope.launch(Dispatchers.Default) {
            getActivityFeedUseCase.executeFlow()
                .catch { e ->
                    // Handle error silently or log
                }
                .collectLatest { feedItems ->
                    withContext(Dispatchers.Main) {
                        viewState = viewState.copy(
                            feedItems = feedItems,
                            isLoading = false,
                            isEmpty = feedItems.isEmpty()
                        )
                    }
                }
        }
    }
}
