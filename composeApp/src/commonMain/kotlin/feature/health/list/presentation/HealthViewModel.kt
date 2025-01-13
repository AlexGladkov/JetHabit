package feature.health.list.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.health.list.presentation.models.HealthEvent
import feature.health.list.presentation.models.HealthViewState
import feature.health.list.presentation.models.TrackerHabitItem
import feature.tracker.data.TrackerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HealthViewModel : BaseViewModel<HealthViewState, Nothing, HealthEvent>(
    initialState = HealthViewState()
) {
    private val habitDao = Inject.instance<HabitDao>()
    private val trackerDao = Inject.instance<TrackerDao>()

    init {
        loadTrackerHabits()
    }

    override fun obtainEvent(viewEvent: HealthEvent) {
        // No events to handle currently
    }

    private fun loadTrackerHabits() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(isLoading = true)
            }

            try {
                val habits = habitDao.getAll().filter { it.type == HabitType.TRACKER }
                val trackerHabits = habits.map { habit ->
                    val history = trackerDao.getAllForHabit(habit.id)
                    TrackerHabitItem(
                        id = habit.id,
                        title = habit.title.lowercase().replaceFirstChar { it.uppercase() },
                        measurement = habit.measurement,
                        lastValue = history.firstOrNull()?.value,
                        values = history.map { it.value },
                        dates = history.map { it.timestamp }
                    )
                }

                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        habits = trackerHabits,
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