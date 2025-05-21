package feature.chat.presentation.models

sealed class ChatEvent {
    data class MessageChanged(val text: String) : ChatEvent()
    data class ApiKeyChanged(val key: String) : ChatEvent()
    data object SendClicked : ChatEvent()
}
