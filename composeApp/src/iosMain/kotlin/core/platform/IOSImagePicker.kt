package core.platform

import kotlinx.cinterop.*
import platform.Foundation.NSURL
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSImagePicker : ImagePicker {
    private var currentViewController: UIViewController? = null
    
    fun setViewController(viewController: UIViewController) {
        currentViewController = viewController
    }
    
    override suspend fun pickImage(): String? = suspendCoroutine { continuation ->
        val imagePickerController = UIImagePickerController()
        val delegate = ImagePickerDelegate { url ->
            continuation.resume(url)
        }
        
        imagePickerController.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        imagePickerController.delegate = delegate
        
        currentViewController?.presentViewController(
            imagePickerController,
            animated = true,
            completion = null
        )
    }
    
    override suspend fun takePhoto(): String? = suspendCoroutine { continuation ->
        val imagePickerController = UIImagePickerController()
        val delegate = ImagePickerDelegate { url ->
            continuation.resume(url)
        }
        
        imagePickerController.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        imagePickerController.delegate = delegate
        
        currentViewController?.presentViewController(
            imagePickerController,
            animated = true,
            completion = null
        )
    }
}

private class ImagePickerDelegate(
    private val onImagePicked: (String?) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val imageUrl = didFinishPickingMediaWithInfo[UIImagePickerControllerReferenceURL] as? NSURL
        onImagePicked(imageUrl?.absoluteString)
        picker.dismissViewControllerAnimated(true, completion = null)
    }
    
    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onImagePicked(null)
        picker.dismissViewControllerAnimated(true, completion = null)
    }
} 