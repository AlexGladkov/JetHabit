package screens.compose.models

sealed class ComposeError {
    object SendingGeneric : ComposeError()
}