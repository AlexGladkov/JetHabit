package ru.alexgladkov.jetpackcomposedemo.screens.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.alexgladkov.jetpackcomposedemo.base.EventHandler
import ru.alexgladkov.jetpackcomposedemo.data.features.habit.HabitEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.habit.HabitRepository
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeEvent
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel(), EventHandler<ComposeEvent> {

    private val _composeViewState: MutableLiveData<ComposeViewState> =
        MutableLiveData(ComposeViewState.ViewStateInitial())
    val composeViewState: LiveData<ComposeViewState> = _composeViewState

    override fun obtainEvent(event: ComposeEvent) {
        when (val currentViewState = _composeViewState.value) {
            is ComposeViewState.ViewStateInitial -> reduce(event, currentViewState)
        }
    }

    private fun reduce(event: ComposeEvent, currentState: ComposeViewState.ViewStateInitial) {
        when (event) {
            is ComposeEvent.TitleChanged -> _composeViewState.postValue(
                currentState.copy(habitTitle = event.newValue)
            )

            is ComposeEvent.CheckboxClicked -> _composeViewState.postValue(
                currentState.copy(isGoodHabit = event.newValue)
            )

            ComposeEvent.SaveClicked -> saveHabitToDatabase(currentState)
        }
    }

    private fun saveHabitToDatabase(state: ComposeViewState.ViewStateInitial) {
        viewModelScope.launch {
            _composeViewState.postValue(state.copy(isSending = true))

            try {
                habitRepository.addNewHabit(
                    HabitEntity(
                        title = state.habitTitle,
                        isGood = state.isGoodHabit
                    )
                )

                _composeViewState.postValue(ComposeViewState.ViewStateSuccess)
            } catch (e: Exception) {
                _composeViewState.postValue(state.copy(isSending = false))
            }
        }
    }
}