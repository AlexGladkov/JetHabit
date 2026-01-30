package feature.detail.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.detail.domain.DeleteHabitUseCase
import feature.detail.domain.GetDetailInfoUseCase
import feature.detail.domain.UpdateHabitUseCase
import feature.detail.presentation.models.DateSelectionState
import feature.detail.presentation.models.DetailEvent
import feature.detail.presentation.models.DetailViewState
import feature.habits.data.HabitType
import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import utils.CalendarDays
import java.util.UUID

class DetailComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val habitId: String,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val getDetailInfoUseCase: GetDetailInfoUseCase by di.instance()
    private val deleteHabitUseCase: DeleteHabitUseCase by di.instance()
    private val updateHabitUseCase: UpdateHabitUseCase by di.instance()
    private val trackerDao: TrackerDao by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(DetailViewState(habitId = habitId))
    val state: Value<DetailViewState> = _state

    init {
        fetchDetailedInformation()
    }

    fun onEvent(viewEvent: DetailEvent) {
        when (viewEvent) {
            DetailEvent.CloseScreen -> onNavigateBack()
            DetailEvent.DeleteItem -> deleteItem()
            DetailEvent.SaveChanges -> applyChanges()
            DetailEvent.StartDateClicked -> _state.value = _state.value.copy(dateSelectionState = DateSelectionState.Start)
            DetailEvent.EndDateClicked -> _state.value = _state.value.copy(dateSelectionState = DateSelectionState.End)
            is DetailEvent.DateSelected -> selectDate(viewEvent.value)
            is DetailEvent.NewValueChanged -> parseTrackerValue(viewEvent.value)
        }
    }

    private fun parseTrackerValue(value: String?) {
        val newValue = if (value.isNullOrEmpty()) {
            null
        } else {
            value.toDoubleOrNull()
        }

        _state.value = _state.value.copy(newValue = newValue)
    }

    private fun fetchDetailedInformation() {
        scope.launch(Dispatchers.Default) {
            val details = getDetailInfoUseCase.execute(habitId)
            val currentValue = if (details.type == HabitType.TRACKER) {
                trackerDao.getLatestValueFor(habitId)?.value
            } else null

            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(
                    itemTitle = details.habitTitle,
                    startDate = details.startDate,
                    endDate = details.endDate,
                    start = details.start,
                    end = details.end,
                    daysToCheck = details.daysToCheck,
                    isGood = details.isHabitGood,
                    type = details.type,
                    currentValue = currentValue
                )
            }
        }
    }

    private fun deleteItem() {
        scope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(isDeleting = true)
            }

            try {
                deleteHabitUseCase.execute(_state.value.habitId)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(isDeleting = true)
                }
            }

            withContext(Dispatchers.Main) {
                onNavigateBack()
            }
        }
    }

    private fun selectDate(value: LocalDate) {
        when (_state.value.dateSelectionState) {
            DateSelectionState.None -> {}
            DateSelectionState.Start -> {
                _state.value = _state.value.copy(start = value, startDate = CalendarDays.Custom(value.toString()))
            }

            DateSelectionState.End -> _state.value =
                _state.value.copy(end = value, endDate = CalendarDays.Custom(value.toString()))
        }

        _state.value = _state.value.copy(dateSelectionState = DateSelectionState.None)
    }

    private fun applyChanges() {
        scope.launch(Dispatchers.Default) {
            try {
                updateHabitUseCase.execute(
                    habitId = _state.value.habitId,
                    habitTitle = _state.value.itemTitle,
                    startDate = _state.value.start,
                    endDate = _state.value.end,
                    daysToCheck = _state.value.daysToCheck.joinToString(","),
                    isGood = _state.value.isGood
                )

                // Update tracker value if changed
                val currentNewValue = _state.value.newValue
                if (_state.value.type == HabitType.TRACKER && currentNewValue != null) {
                    trackerDao.insert(
                        TrackerEntity(
                            id = UUID.randomUUID().toString(),
                            habitId = _state.value.habitId,
                            timestamp = Clock.System.now().toString(),
                            value = currentNewValue
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    onNavigateBack()
                }
            } catch (e: IllegalStateException) {
                println(e.message)
            } catch (e: NullPointerException) {
                println(e.message)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}
