package core.platform

expect class ShareService {
    suspend fun shareImage(imageBytes: ByteArray, text: String)
}
