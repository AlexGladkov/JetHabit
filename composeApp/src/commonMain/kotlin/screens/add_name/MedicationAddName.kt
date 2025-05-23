package screens.add_name

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import screens.add_name.models.MedicationAddNameAction
import screens.add_name.models.MedicationAddNameEvent
import tech.mobiledeveloper.jethabit.resources.*
import ui.themes.JetHabitTheme
import ui.themes.components.JetHabitButton

@Composable
internal fun MedicationAddName(
    viewModel: MedicationAddNameViewModel = viewModel { MedicationAddNameViewModel() }
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    Column(
        modifier = Modifier.clickable { keyboard?.hide() }.fillMaxSize()
            .background(JetHabitTheme.colors.secondaryBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(
                    start = 16.dp, end = 16.dp,
                    top = 20.dp
                )
                    .align(Alignment.CenterEnd)
                    .clickable {

                    },
                text = stringResource(Res.string.action_close),
                color = JetHabitTheme.colors.tintColor,
                fontSize = 16.sp
            )
        }

        Image(
            modifier = Modifier.padding(top = 56.dp).size(56.dp),
            painter = painterResource(Res.drawable.medication_add),
            contentDescription = "Medication Add Icon"
        )

        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(Res.string.medication_add_name),
            fontSize = 32.sp,
            color = JetHabitTheme.colors.primaryText,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        OutlinedTextField(
            modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth().height(48.dp),
            value = viewState.name,
            onValueChange = {
//                viewModel.obtainEvent(MedicationAddNameEvent.ChangeName(it))
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = JetHabitTheme.colors.primaryBackground,
                unfocusedBorderColor = JetHabitTheme.colors.primaryBackground,
                disabledBorderColor = JetHabitTheme.colors.primaryBackground,
                errorBorderColor = JetHabitTheme.colors.primaryBackground,
                backgroundColor = JetHabitTheme.colors.primaryBackground,
                textColor = JetHabitTheme.colors.primaryText,
                cursorColor = JetHabitTheme.colors.controlColor
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        JetHabitButton(
            modifier = Modifier.padding(vertical = 44.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            text = stringResource(Res.string.action_next),
            enabled = viewState.isNext,
            onClick = {
                viewModel.obtainEvent(MedicationAddNameEvent.NextClicked)
            })
    }

    when (viewAction) {
        MedicationAddNameAction.NextClicked -> {
            viewModel.obtainEvent(viewEvent = MedicationAddNameEvent.ActionInvoked)
        }

        null -> {}
    }
}