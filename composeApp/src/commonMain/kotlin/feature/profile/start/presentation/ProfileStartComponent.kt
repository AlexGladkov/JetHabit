package feature.profile.start.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import core.database.dao.UserProfileDao
import core.platform.ImagePicker
import feature.profile.start.ui.models.ProfileEvent
import feature.profile.start.ui.models.ProfileViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class ProfileStartComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onNavigateToEdit: () -> Unit,
    private val onNavigateToSettings: () -> Unit,
    private val onNavigateToProjects: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val imagePicker: ImagePicker by di.instance()
    private val userProfileDao: UserProfileDao by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(ProfileViewState())
    val state: Value<ProfileViewState> = _state

    init {
        onEvent(ProfileEvent.LoadProfile)
    }

    fun onEvent(viewEvent: ProfileEvent) {
        when (viewEvent) {
            is ProfileEvent.PickImageFromLibrary -> pickImage()
            is ProfileEvent.TakePhoto -> takePhoto()
            ProfileEvent.EditProfileClicked -> onNavigateToEdit()
            ProfileEvent.OpenSettings -> onNavigateToSettings()
            ProfileEvent.OpenProjects -> onNavigateToProjects()
            ProfileEvent.LoadProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userProfileDao.getUserProfile().collect { profile ->
                _state.value = _state.value.copy(
                    name = profile?.name.orEmpty(),
                    email = profile?.email.orEmpty(),
                    avatarUrl = profile?.avatarUri,
                    isLoading = false
                )
            }
        }
    }

    private fun pickImage() {
        scope.launch {
            val uri = imagePicker.pickImage()
            if (uri != null) {
                userProfileDao.updateAvatar(uri)
            }
        }
    }

    private fun takePhoto() {
        scope.launch {
            val uri = imagePicker.takePhoto()
            if (uri != null) {
                userProfileDao.updateAvatar(uri)
            }
        }
    }
}
