package tech.mobiledeveloper.jethabit

import App
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import core.platform.AndroidImagePicker
import di.PlatformConfiguration
import di.PlatformSDK

class MainActivity : ComponentActivity() {
    private lateinit var imagePicker: AndroidImagePicker
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // First create empty callbacks that will be updated once imagePicker is created
        var imagePickerCallback: ((Int, String?) -> Unit)? = null
        var photoTakerCallback: ((Int, String?) -> Unit)? = null
        
        // Register launchers first with temporary callbacks
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            imagePickerCallback?.invoke(
                result.resultCode,
                result.data?.data?.toString()
            )
        }

        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            photoTakerCallback?.invoke(
                result.resultCode,
                result.data?.data?.toString()
            )
        }

        // Create ImagePicker with registered launchers
        imagePicker = AndroidImagePicker(
            pickImageLauncher = pickImageLauncher,
            takePhotoLauncher = takePhotoLauncher
        )

        // Now update the callbacks to use the created imagePicker
        imagePickerCallback = { resultCode, uri ->
            imagePicker.handlePickImageResult(resultCode, uri)
        }
        photoTakerCallback = { resultCode, uri ->
            imagePicker.handleTakePhotoResult(resultCode, uri)
        }
        
        // Initialize SDK with configuration including imagePicker
        PlatformSDK.init(
            configuration = PlatformConfiguration(
                application = application,
                activity = this,
                imagePicker = imagePicker
            )
        )
        
        setContent {
            App()
        }
    }
} 