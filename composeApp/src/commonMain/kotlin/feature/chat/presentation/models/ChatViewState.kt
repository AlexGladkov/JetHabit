package feature.chat.presentation.models

data class ChatViewState(
    val messages: List<ChatMessage> = emptyList(),
    val currentMessage: String = "",
    val apiKey: String = ""
)
