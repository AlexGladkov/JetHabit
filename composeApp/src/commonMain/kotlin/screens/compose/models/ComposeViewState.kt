package screens.compose.models

sealed class ComposeViewState {

    data class ViewStateInitial(
        val habitTitle: String = "",
        val isGoodHabit: Boolean = true,
        val isSending: Boolean = false,
        val sendingError: ComposeError? = null
    ) : ComposeViewState()

    object ViewStateSuccess : ComposeViewState()
}