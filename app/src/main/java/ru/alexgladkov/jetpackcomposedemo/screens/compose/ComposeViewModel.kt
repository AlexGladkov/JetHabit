package ru.alexgladkov.jetpackcomposedemo.screens.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.alexgladkov.jetpackcomposedemo.base.EventHandler
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabitEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitRepository
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeEvent
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val habbitRepository: HabbitRepository
) : ViewModel(), EventHandler<ComposeEvent> {

    private val _composeViewState: MutableLiveData<ComposeViewState> =
        MutableLiveData(ComposeViewState.ViewStateInitial())
    val composeViewState: LiveData<ComposeViewState> = _composeViewState

    override fun obtainEvent(event: ComposeEvent) {
        when (val currentViewState = _composeViewState.value) {
            is ComposeViewState.ViewStateInitial -> reduce(event, currentViewState)
        }
    }

    private fun reduce(event: ComposeEvent, currentState: ComposeViewState.ViewStateInitial, ) {
        when (event) {
            is ComposeEvent.TitleChanged -> _composeViewState.postValue(
                currentState.copy(habbitTitle = event.newValue)
            )

            is ComposeEvent.CheckboxClicked -> _composeViewState.postValue(
                currentState.copy(isGoodHabbit = event.newValue)
            )

            ComposeEvent.SaveClicked -> saveHabbitToDatabase(currentState)
        }
    }

    private fun saveHabbitToDatabase(state: ComposeViewState.ViewStateInitial) {
        viewModelScope.launch {
            _composeViewState.postValue(state.copy(isSending = true))

            try {
                habbitRepository.addNewHabbit(
                    HabitEntity(
                        title = state.habbitTitle,
                        isGood = state.isGoodHabbit
                    )
                )

                _composeViewState.postValue(ComposeViewState.ViewStateSuccess)
            } catch (e: Exception) {
                _composeViewState.postValue(state.copy(isSending = false))
            }
        }
    }
}