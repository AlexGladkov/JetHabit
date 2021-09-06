package ru.alexgladkov.jetpackcomposedemo.screens.daily

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabbitRepository
import javax.inject.Inject

@HiltViewModel
class DailyViewModel @Inject constructor(
    val habbitRepository: HabbitRepository
): ViewModel() {

}