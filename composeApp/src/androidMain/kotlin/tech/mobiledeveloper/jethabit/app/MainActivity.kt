package tech.mobiledeveloper.jethabit.app

import App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import data.features.settings.SettingsEventBus
import core.database.getDatabaseBuilder
import core.platform.AndroidImagePicker
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK

class MainActivity : AppCompatActivity() {
    companion object {
        internal var settingsEventBusObserverForTesting: ((SettingsEventBus) -> Unit)? = null
        internal var lastCreatedActivity: MainActivity? = null
        internal var lastImagePicker: AndroidImagePicker? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Register image picker launchers
        var imagePickerCallback: ((Int, String?) -> Unit)? = null
        var photoTakerCallback: ((Int, String?) -> Unit)? = null
        
        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            imagePickerCallback?.invoke(
                result.resultCode,
                result.data?.data?.toString()
            )
        }

        val takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            photoTakerCallback?.invoke(
                result.resultCode,
                result.data?.data?.toString()
            )
        }

        // Create ImagePicker
        val imagePicker = AndroidImagePicker(
            ownerActivity = this,
            pickImageLauncher = pickImageLauncher,
            takePhotoLauncher = takePhotoLauncher
        )

        // Set up callbacks
        imagePickerCallback = { resultCode, uri ->
            imagePicker.handlePickImageResult(resultCode, uri)
        }
        photoTakerCallback = { resultCode, uri ->
            imagePicker.handleTakePhotoResult(resultCode, uri)
        }

        lastCreatedActivity = this
        lastImagePicker = imagePicker
        val appDatabase = (application as JetHabitApp).database
        PlatformSDK.init(
            configuration = PlatformConfiguration(
                application = application,
                activity = this@MainActivity,
                imagePicker = imagePicker
            ),
            appDatabase = appDatabase
        )

        setContent {
            CompositionLocalProvider(
                LocalPlatform provides Platform.Android
            ) {
                App(
                    settingsEventBus = (application as JetHabitApp).settingsEventBus,
                    onSettingsEventBusReady = settingsEventBusObserverForTesting
                )
            }
        }
    }
}