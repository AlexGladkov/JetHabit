package feature.health.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.habits.data.HabitDao
import feature.health.presentation.models.HealthViewState
import feature.health.presentation.models.TrackerHistoryItem
import feature.tracker.data.TrackerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HealthViewModel : BaseViewModel<HealthViewState, Nothing, Nothing>(
    initialState = HealthViewState()
) {
    private val habitDao = Inject.instance<HabitDao>()
    private val trackerDao = Inject.instance<TrackerDao>()

    init {
        loadTrackerHistory()
    }

    override fun obtainEvent(viewEvent: Nothing) {
        TODO("Not yet implemented")
    }

    private fun loadTrackerHistory() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(isLoading = true)
            }

            try {
                val habits = habitDao.getAll()
                val history = habits.mapNotNull { habit ->
                    trackerDao.getLatestValueFor(habit.id)?.let { tracker ->
                        TrackerHistoryItem(
                            title = habit.title,
                            value = tracker.value,
                            date = tracker.timestamp,
                            measurement = habit.measurement
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        trackerHistory = history,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(isLoading = false)
                }
            }
        }
    }
} 