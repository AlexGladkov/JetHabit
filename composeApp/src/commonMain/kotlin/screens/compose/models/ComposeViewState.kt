package screens.compose.models

data class ComposeViewState(
    val habitTitle: String = "",
    val isGoodHabit: Boolean = true,
    val isSending: Boolean = false,
    val sendingError: ComposeError? = null,
    val isSent: Boolean = false
)