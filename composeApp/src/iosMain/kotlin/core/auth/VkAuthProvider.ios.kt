package core.auth

import cocoapods.VKID.VKIDAuthCallback
import cocoapods.VKID.VKIDAuthFail
import cocoapods.VKID.VKIDAuthSuccessData
import cocoapods.VKID.VKIDSDK
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
actual class VkAuthProvider : AuthProvider {

    override val providerName: String = "vk"

    private var isSignedIn = false

    init {
        // Initialize VK ID SDK for iOS
        // VKIDSDK.shared.configure()
    }

    override suspend fun signIn(): AuthResult = suspendCancellableCoroutine { continuation ->
        try {
            val vkid = VKIDSDK.shared

            // Start VK ID login flow
            vkid.authorizeWithCallback(object : VKIDAuthCallback {
                override fun onSuccess(data: VKIDAuthSuccessData) {
                    isSignedIn = true

                    val user = data.user
                    val firstName = user.firstName ?: ""
                    val lastName = user.lastName ?: ""
                    val displayName = "$firstName $lastName".trim()

                    continuation.resume(
                        AuthResult.Success(
                            userId = user.id.toString(),
                            displayName = displayName.ifEmpty { "VK User" },
                            email = user.email,
                            avatarUrl = user.photo200
                        )
                    )
                }

                override fun onFail(error: NSError) {
                    val errorDescription = error.localizedDescription

                    // Check if user cancelled
                    if (error.code == -1L) {
                        continuation.resume(AuthResult.Cancelled)
                    } else {
                        continuation.resume(
                            AuthResult.Error(errorDescription ?: "VK login failed")
                        )
                    }
                }

                override fun onCancel() {
                    continuation.resume(AuthResult.Cancelled)
                }
            })
        } catch (e: Exception) {
            continuation.resume(AuthResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun signOut() {
        try {
            VKIDSDK.shared.logout()
            isSignedIn = false
        } catch (e: Exception) {
            // Log error but don't throw
        }
    }

    override fun isSignedIn(): Boolean {
        return isSignedIn
    }
}
