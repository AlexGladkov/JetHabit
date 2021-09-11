package ru.alexgladkov.jetpackcomposedemo.screens.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitRepository
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeEvent
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    val habbitRepository: HabbitRepository
) : ViewModel() {

    private val _composeViewState: MutableLiveData<ComposeViewState> = MutableLiveData(ComposeViewState())
    val composeViewState: LiveData<ComposeViewState> = _composeViewState

    fun obtainEvent(event: ComposeEvent) {
        when (event) {
            is ComposeEvent.TitleChanged -> _composeViewState.postValue(
                _composeViewState.value?.copy(
                    habbitTitle = event.newValue
                )
            )

            is ComposeEvent.CheckboxClicked -> _composeViewState.postValue(
                _composeViewState.value?.copy(
                    isGoodHabbit = event.newValue
                )
            )
        }
    }
}