package feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import feature.chat.presentation.models.ChatEvent
import feature.chat.presentation.models.ChatMessage
import feature.chat.presentation.models.ChatViewState
import org.kodein.di.DI
import org.kodein.di.DIAware

class ChatComponent(
    componentContext: ComponentContext,
    override val di: DI
) : ComponentContext by componentContext, DIAware {

    private val _state = MutableValue(ChatViewState())
    val state: Value<ChatViewState> = _state

    fun onEvent(viewEvent: ChatEvent) {
        when (viewEvent) {
            is ChatEvent.MessageChanged -> _state.value = _state.value.copy(currentMessage = viewEvent.text)
            is ChatEvent.ApiKeyChanged -> _state.value = _state.value.copy(apiKey = viewEvent.key)
            ChatEvent.SendClicked -> sendMessage()
        }
    }

    private fun sendMessage() {
        val text = _state.value.currentMessage.trim()
        if (text.isEmpty()) return
        val updatedMessages = _state.value.messages + ChatMessage(text, true)
        val reply = generateReply(text)
        _state.value = _state.value.copy(
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
