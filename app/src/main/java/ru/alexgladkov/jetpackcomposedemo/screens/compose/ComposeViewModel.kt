package ru.alexgladkov.jetpackcomposedemo.screens.compose

import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitRepository
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeEvent
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val habbitRepository: HabbitRepository
) : ViewModel() {

    private val _composeViewState: MutableLiveData<ComposeViewState> =
        MutableLiveData(ComposeViewState.ViewStateInitial())
    val composeViewState: LiveData<ComposeViewState> = _composeViewState

    fun obtainEvent(event: ComposeEvent) {
        when (val currentViewState = _composeViewState.value) {
            is ComposeViewState.ViewStateInitial -> dispatchInitialState(currentViewState, event)
            is ComposeViewState.ViewStateSuccess -> dispatchSuccessState(currentViewState, event)
        }
    }

    private fun dispatchInitialState(state: ComposeViewState.ViewStateInitial, event: ComposeEvent) {
        when (event) {
            is ComposeEvent.TitleChanged -> _composeViewState.postValue(
                state.copy(habbitTitle = event.newValue)
            )

            is ComposeEvent.CheckboxClicked -> _composeViewState.postValue(
                state.copy(isGoodHabbit = event.newValue)
            )

            ComposeEvent.SaveClicked -> saveHabbitToDatabase(state)
        }
    }

    private fun dispatchSuccessState(state: ComposeViewState.ViewStateSuccess, event: ComposeEvent) {
        when (event) {

        }
    }

    private fun saveHabbitToDatabase(state: ComposeViewState.ViewStateInitial) {
        viewModelScope.launch {
//            habbitRepository.addNewHabbit(
//                HabbitEntity(
//                    title = state.habbitTitle,
//                    isGood = state.isGoodHabbit
//                )
//            )

            _composeViewState.postValue(state.copy(isSending = true))
            delay(3000)
            _composeViewState.postValue(ComposeViewState.ViewStateSuccess)
        }
    }
}