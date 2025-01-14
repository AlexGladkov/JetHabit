package core.platform

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidImagePicker(
    private val pickImageLauncher: ActivityResultLauncher<Intent>,
    private val takePhotoLauncher: ActivityResultLauncher<Intent>
) : ImagePicker {
    private var photoCallback: ((String?) -> Unit)? = null
    private var imageCallback: ((String?) -> Unit)? = null

    override suspend fun pickImage(): String? = suspendCancellableCoroutine { continuation ->
        imageCallback = { uri ->
            continuation.resume(uri)
        }
        
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    override suspend fun takePhoto(): String? = suspendCancellableCoroutine { continuation ->
        photoCallback = { uri ->
            continuation.resume(uri)
        }
        
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoLauncher.launch(intent)
    }

    fun handlePickImageResult(resultCode: Int, uri: String?) {
        if (resultCode == Activity.RESULT_OK) {
            imageCallback?.invoke(uri)
        } else {
            imageCallback?.invoke(null)
        }
        imageCallback = null
    }

    fun handleTakePhotoResult(resultCode: Int, uri: String?) {
        if (resultCode == Activity.RESULT_OK) {
            photoCallback?.invoke(uri)
        } else {
            photoCallback?.invoke(null)
        }
        photoCallback = null
    }
} 