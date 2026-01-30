package feature.profile.edit.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import core.database.dao.UserProfileDao
import core.database.entity.UserProfile
import core.utils.isValidEmail
import feature.profile.edit.ui.models.EditProfileEvent
import feature.profile.edit.ui.models.EditProfileViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class EditProfileComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val userProfileDao: UserProfileDao by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(EditProfileViewState())
    val state: Value<EditProfileViewState> = _state

    init {
        loadProfile()
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }
            is EditProfileEvent.EmailChanged -> {
                _state.value = _state.value.copy(
                    email = event.email,
                    isEmailValid = event.email.isValidEmail()
                )
            }
            EditProfileEvent.SaveClicked -> saveProfile()
            EditProfileEvent.BackClicked -> onNavigateBack()
            EditProfileEvent.LoadProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userProfileDao.getUserProfile().collect { profile ->
                profile?.let {
                    _state.value = _state.value.copy(
                        name = it.name,
                        email = it.email,
                        isEmailValid = it.email.isValidEmail(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        val currentState = _state.value
        if (!currentState.isEmailValid) {
            _state.value = _state.value.copy(showEmailError = true)
            return
        }

        scope.launch {
            _state.value = _state.value.copy(isSaving = true)
            userProfileDao.insertOrUpdateProfile(
                UserProfile(
                    name = currentState.name,
                    email = currentState.email,
                    phoneNumber = "",  // TODO: Add phone number field
                    avatarUri = null   // TODO: Add avatar handling
                )
            )
            _state.value = _state.value.copy(isSaving = false)
            onNavigateBack()
        }
    }
}
