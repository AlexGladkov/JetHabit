package feature.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.chat.presentation.ChatComponent
import feature.chat.presentation.models.ChatEvent
import feature.chat.presentation.models.ChatMessage
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.chat_api_key_hint
import tech.mobiledeveloper.jethabit.resources.chat_message_hint
import tech.mobiledeveloper.jethabit.resources.chat_send
import tech.mobiledeveloper.jethabit.resources.chat_title
import ui.themes.JetHabitTheme
import ui.themes.components.JetHabitButton

@Composable
fun ChatScreen(
    component: ChatComponent
) {
    val viewState by component.state.subscribeAsState()

    ChatView(viewState = viewState) { component.onEvent(it) }
}

@Composable
private fun ChatView(viewState: feature.chat.presentation.models.ChatViewState, eventHandler: (ChatEvent) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(JetHabitTheme.shapes.padding)) {
            OutlinedTextField(
                value = viewState.apiKey,
                onValueChange = { eventHandler(ChatEvent.ApiKeyChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.chat_api_key_hint)) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = JetHabitTheme.colors.primaryText,
                    focusedBorderColor = JetHabitTheme.colors.tintColor,
                    unfocusedBorderColor = JetHabitTheme.colors.secondaryText,
                    focusedLabelColor = JetHabitTheme.colors.tintColor,
                    unfocusedLabelColor = JetHabitTheme.colors.secondaryText
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(viewState.messages) { message ->
                    ChatRow(message)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewState.currentMessage,
                    onValueChange = { eventHandler(ChatEvent.MessageChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(Res.string.chat_message_hint)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = JetHabitTheme.colors.primaryText,
                        focusedBorderColor = JetHabitTheme.colors.tintColor,
                        unfocusedBorderColor = JetHabitTheme.colors.secondaryText,
                        focusedLabelColor = JetHabitTheme.colors.tintColor,
                        unfocusedLabelColor = JetHabitTheme.colors.secondaryText
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                JetHabitButton(
                    onClick = { eventHandler(ChatEvent.SendClicked) },
                    text = stringResource(Res.string.chat_send)
                )
            }
        }
    }
}

@Composable
private fun ChatRow(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (message.isUser) JetHabitTheme.colors.tintColor else JetHabitTheme.colors.secondaryBackground
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp),
                color = if (message.isUser) MaterialTheme.colors.onPrimary else JetHabitTheme.colors.primaryText
            )
        }
    }
}
