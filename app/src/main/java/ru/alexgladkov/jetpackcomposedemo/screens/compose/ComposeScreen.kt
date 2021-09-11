package ru.alexgladkov.jetpackcomposedemo.screens.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeEvent
import ru.alexgladkov.jetpackcomposedemo.screens.compose.models.ComposeViewState
import ru.alexgladkov.jetpackcomposedemo.screens.compose.views.ComposeViewInitial
import ru.alexgladkov.jetpackcomposedemo.screens.compose.views.ComposeViewSuccess
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ComposeScreen(
    modifier: Modifier,
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