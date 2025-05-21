package feature.chat.presentation

import base.BaseViewModel
import feature.chat.presentation.models.ChatAction
import feature.chat.presentation.models.ChatEvent
import feature.chat.presentation.models.ChatMessage
import feature.chat.presentation.models.ChatViewState

class ChatViewModel : BaseViewModel<ChatViewState, ChatAction, ChatEvent>(
    initialState = ChatViewState()
) {
    override fun obtainEvent(viewEvent: ChatEvent) {
        when (viewEvent) {
            is ChatEvent.MessageChanged -> viewState = viewState.copy(currentMessage = viewEvent.text)
            is ChatEvent.ApiKeyChanged -> viewState = viewState.copy(apiKey = viewEvent.key)
            ChatEvent.SendClicked -> sendMessage()
        }
    }

    private fun sendMessage() {
        val text = viewState.currentMessage.trim()
        if (text.isEmpty()) return
        val updatedMessages = viewState.messages + ChatMessage(text, true)
        val reply = generateReply(text)
        viewState = viewState.copy(
            messages = updatedMessages + ChatMessage(reply, false),
            currentMessage = ""
        )
    }

    private fun generateReply(message: String): String {
        val calories = message.filter { it.isDigit() }.toIntOrNull()
        return if (calories != null) {
            "You logged $calories kcal. Consider adjusting your meal plan if this exceeds your goal."
        } else {
            "Thanks for the message!"
        }
    }
}
