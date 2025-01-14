package core.platform

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DesktopImagePicker : ImagePicker {
    override suspend fun pickImage(): String? = withContext(Dispatchers.IO) {
        val fileChooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter("Image files", "jpg", "jpeg", "png")
        }
        
        return@withContext when (fileChooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> fileChooser.selectedFile.toURI().toString()
            else -> null
        }
    }
    
    override suspend fun takePhoto(): String? {
        // Desktop doesn't support taking photos directly
        return null
    }
} 