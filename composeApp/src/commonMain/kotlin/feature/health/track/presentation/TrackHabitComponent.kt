package feature.health.track.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.habits.data.HabitDao
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
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.error_empty_value
import tech.mobiledeveloper.jethabit.resources.error_value_exists

class TrackHabitComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val habitId: String,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val habitDao: HabitDao by di.instance()
    private val trackerDao: TrackerDao by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(
        TrackHabitViewState(
            selectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        )
    )
    val state: Value<TrackHabitViewState> = _state

    init {
        loadHabitDetails()
    }

    fun onEvent(viewEvent: TrackHabitEvent) {
        when (viewEvent) {
            is TrackHabitEvent.NewValueChanged -> parseNewValue(viewEvent.value)
            is TrackHabitEvent.DateSelected -> _state.value = _state.value.copy(selectedDate = viewEvent.date.toString())
            TrackHabitEvent.SaveClicked -> saveNewValue()
            TrackHabitEvent.CloseClicked -> onNavigateBack()
        }
    }

    private fun loadHabitDetails() {
        scope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(isLoading = true)
            }

            try {
                val habit = habitDao.getAll().first { it.id == habitId }

                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        title = habit.title,
                        measurement = habit.measurement,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isLoading = false)
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
        _state.value = _state.value.copy(
            newValue = newValue,
            error = null
        )
    }

    private fun saveNewValue() {
        val currentValue = _state.value.newValue

        if (currentValue == null) {
            _state.value = _state.value.copy(error = Res.string.error_empty_value)
            return
        }

        scope.launch(Dispatchers.Default) {
            // Check if entry for this date already exists
            val existingEntry = trackerDao.getAll()
                .firstOrNull { it.habitId == habitId && it.timestamp == _state.value.selectedDate }

            if (existingEntry != null) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(error = Res.string.error_value_exists)
                }
                return@launch
            }

            trackerDao.insert(
                TrackerEntity(
                    id = UUID.generateUUID().toString(),
                    habitId = habitId,
                    timestamp = _state.value.selectedDate,
                    value = currentValue
                )
            )

            withContext(Dispatchers.Main) {
                onNavigateBack()
            }
        }
    }
}
