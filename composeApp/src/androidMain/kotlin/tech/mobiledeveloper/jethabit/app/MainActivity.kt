package tech.mobiledeveloper.jethabit.app

import App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.defaultComponentContext
import core.database.getDatabaseBuilder
import core.platform.AndroidImagePicker
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import root.RootComponent
import root.RootContent

class MainActivity : AppCompatActivity() {
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

        val appDatabase = getDatabaseBuilder(applicationContext).build()
        PlatformSDK.init(
            configuration = PlatformConfiguration(
                application = application,
                activity = this@MainActivity,
                imagePicker = imagePicker
            ),
            appDatabase = appDatabase
        )

        val rootComponent = RootComponent(
            componentContext = defaultComponentContext(),
            di = PlatformSDK.di
        )

        setContent {
            CompositionLocalProvider(
                LocalPlatform provides Platform.Android
            ) {
                App(rootComponent)
            }
        }
    }
}