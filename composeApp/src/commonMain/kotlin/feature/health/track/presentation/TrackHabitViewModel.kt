package feature.health.track.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.habits.data.HabitDao
import feature.health.track.presentation.models.TrackHabitEvent
import feature.health.track.presentation.models.TrackHabitViewState
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class TrackHabitViewModel(
    private val habitId: String
) : BaseViewModel<TrackHabitViewState, Nothing, TrackHabitEvent>(
    initialState = TrackHabitViewState()
) {
    private val habitDao = Inject.instance<HabitDao>()
    private val trackerDao = Inject.instance<TrackerDao>()

    init {
        loadHabitDetails()
    }

    override fun obtainEvent(viewEvent: TrackHabitEvent) {
        when (viewEvent) {
            is TrackHabitEvent.NewValueChanged -> parseNewValue(viewEvent.value)
            TrackHabitEvent.SaveClicked -> saveNewValue()
            TrackHabitEvent.CloseClicked -> TODO()
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
        viewState = viewState.copy(newValue = newValue)
    }

    private fun saveNewValue() {
        val currentValue = viewState.newValue ?: return

        viewModelScope.launch(Dispatchers.Default) {
            val now = Clock.System.now()
            val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

            trackerDao.insert(
                TrackerEntity(
                    id = UUID.generateUUID().toString(),
                    habitId = habitId,
                    timestamp = date,
                    value = currentValue
                )
            )
        }
    }
} 