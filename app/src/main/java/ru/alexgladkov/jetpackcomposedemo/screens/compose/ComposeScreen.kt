package ru.alexgladkov.jetpackcomposedemo.screens.compose

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeEvent
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import ru.alexgladkov.jetpackcomposedemo.screens.compose.views.ComposeViewInitial
import ru.alexgladkov.jetpackcomposedemo.screens.compose.views.ComposeViewSuccess

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ComposeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    composeViewModel: ComposeViewModel
) {
    val viewState = composeViewModel.composeViewState.observeAsState(initial = ComposeViewState.ViewStateInitial())
    val keyboardController = LocalSoftwareKeyboardController.current

    when (val state = viewState.value) {
        is ComposeViewState.ViewStateInitial -> ComposeViewInitial(
            modifier = modifier,
            state = state,
            onCheckedChange = { composeViewModel.obtainEvent(ComposeEvent.CheckboxClicked(it)) },
            onTitleChanged = { composeViewModel.obtainEvent(ComposeEvent.TitleChanged(it)) },
            onSaveClicked = {
                keyboardController?.hide()
                composeViewModel.obtainEvent(ComposeEvent.SaveClicked)
            }
        )

        is ComposeViewState.ViewStateSuccess -> ComposeViewSuccess(
            modifier = modifier,
            onCloseClick = { navController.popBackStack() }
        )
    }
}