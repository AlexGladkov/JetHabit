package core.platform

interface ImagePicker {
    suspend fun pickImage(): String?
    suspend fun takePhoto(): String?
} 