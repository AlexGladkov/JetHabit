package feature.health.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.habits.data.HabitDao
import feature.habits.data.HabitType
import feature.health.list.presentation.models.HealthViewState
import feature.health.list.presentation.models.TrackerHabitItem
import feature.tracker.data.TrackerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class HealthListComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onHabitSelected: (String) -> Unit,
    private val onCreateClicked: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val habitDao: HabitDao by di.instance()
    private val trackerDao: TrackerDao by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(HealthViewState())
    val state: Value<HealthViewState> = _state

    init {
        loadTrackerHabits()
    }

    fun onHabitClick(habitId: String) {
        onHabitSelected(habitId)
    }

    fun onCreateClick() {
        onCreateClicked()
    }

    private fun loadTrackerHabits() {
        scope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(isLoading = true)
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
                    _state.value = _state.value.copy(
                        habits = trackerHabits,
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
}
