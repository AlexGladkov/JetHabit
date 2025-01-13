package feature.health.track.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.habits.data.HabitDao
import feature.health.track.presentation.models.TrackHabitAction
import feature.health.track.presentation.models.TrackHabitEvent
import feature.health.track.presentation.models.TrackHabitViewState
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.error_empty_value
import tech.mobiledeveloper.jethabit.resources.error_value_exists

class TrackHabitViewModel(
    private val habitId: String
) : BaseViewModel<TrackHabitViewState, TrackHabitAction, TrackHabitEvent>(
    initialState = TrackHabitViewState(
        selectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    )
) {
    private val habitDao = Inject.instance<HabitDao>()
    private val trackerDao = Inject.instance<TrackerDao>()

    init {
        loadHabitDetails()
    }

    override fun obtainEvent(viewEvent: TrackHabitEvent) {
        when (viewEvent) {
            is TrackHabitEvent.NewValueChanged -> parseNewValue(viewEvent.value)
            is TrackHabitEvent.DateSelected -> viewState = viewState.copy(selectedDate = viewEvent.date.toString())
            TrackHabitEvent.SaveClicked -> saveNewValue()
            TrackHabitEvent.CloseClicked -> viewAction = TrackHabitAction.NavigateBack
        }
    }

    private fun loadHabitDetails() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(isLoading = true)
            }

            try {
                val habit = habitDao.getAll().first { it.id == habitId }

                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(
                        title = habit.title,
                        measurement = habit.measurement,
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

    private fun parseNewValue(value: String) {
        val newValue = if (value.isEmpty()) {
            null
        } else {
            value.toDoubleOrNull()
        }
        viewState = viewState.copy(
            newValue = newValue,
            error = null
        )
    }

    private fun saveNewValue() {
        val currentValue = viewState.newValue
        
        if (currentValue == null) {
            viewState = viewState.copy(error = Res.string.error_empty_value)
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            // Check if entry for this date already exists
            val existingEntry = trackerDao.getAll()
                .firstOrNull { it.habitId == habitId && it.timestamp == viewState.selectedDate }

            if (existingEntry != null) {
                withContext(Dispatchers.Main) {
                    viewState = viewState.copy(error = Res.string.error_value_exists)
                }
                return@launch
            }

            trackerDao.insert(
                TrackerEntity(
                    id = UUID.generateUUID().toString(),
                    habitId = habitId,
                    timestamp = viewState.selectedDate,
                    value = currentValue
                )
            )
            
            withContext(Dispatchers.Main) {
                viewAction = TrackHabitAction.NavigateBack
            }
        }
    }
} 