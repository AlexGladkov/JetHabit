package screens.add_name

import base.BaseViewModel
import screens.add_name.models.MedicationAddNameAction
import screens.add_name.models.MedicationAddNameEvent
import screens.add_name.models.MedicationAddNameViewState

class MedicationAddNameViewModel: BaseViewModel<MedicationAddNameViewState, MedicationAddNameAction, MedicationAddNameEvent>(
    initialState = MedicationAddNameViewState()
) {

    override fun obtainEvent(viewEvent: MedicationAddNameEvent) {
        when(viewEvent) {
            is MedicationAddNameEvent.ChangeName -> {
                viewState = viewState.copy(name = viewEvent.value, isNext = viewEvent.value.isNotBlank())
            }
            MedicationAddNameEvent.NextClicked -> viewAction = MedicationAddNameAction.NextClicked
            MedicationAddNameEvent.ActionInvoked -> viewAction = null
        }
    }
}