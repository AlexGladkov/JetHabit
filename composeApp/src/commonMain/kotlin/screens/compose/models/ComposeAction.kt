package screens.compose.models

sealed class ComposeAction {
    data object Success : ComposeAction()
    data object Error : ComposeAction()
    data object CloseScreen : ComposeAction()
}
