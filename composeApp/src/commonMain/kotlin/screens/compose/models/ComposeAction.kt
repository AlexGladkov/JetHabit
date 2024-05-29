package screens.compose.models

sealed class ComposeAction {
    data object CloseScreen : ComposeAction()
    data object ShowSuccess : ComposeAction()
}
