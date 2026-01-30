package core.auth

import android.app.Activity
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class VkAuthProvider(
    private val activity: Activity
) : AuthProvider {

    override val providerName: String = "vk"

    private var isSignedIn = false

    init {
        // Initialize VK ID SDK if needed
        // VKID.init(context)
    }

    override suspend fun signIn(): AuthResult = suspendCancellableCoroutine { continuation ->
        try {
            // Get the VKID instance
            val vkid = VKID.instance

            // Start the OneTap login flow
            vkid.authorize(
                activity = activity,
                callback = object : VKIDAuthCallback {
                    override fun onSuccess(accessToken: com.vk.id.AccessToken) {
                        isSignedIn = true

                        // Extract user data from the token
                        val userData = accessToken.userData

                        continuation.resume(
                            AuthResult.Success(
                                userId = userData.userId.toString(),
                                displayName = "${userData.firstName} ${userData.lastName}".trim(),
                                email = userData.email,
                                avatarUrl = userData.photo200
                            )
                        )
                    }

                    override fun onFail(fail: VKIDAuthFail) {
                        when (fail) {
                            is VKIDAuthFail.Canceled -> {
                                continuation.resume(AuthResult.Cancelled)
                            }
                            is VKIDAuthFail.Failed -> {
                                continuation.resume(
                                    AuthResult.Error(
                                        fail.description ?: "VK login failed"
                                    )
                                )
                            }
                            is VKIDAuthFail.NoBrowserAvailable -> {
                                continuation.resume(
                                    AuthResult.Error("No browser available for VK login")
                                )
                            }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            continuation.resume(AuthResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun signOut() {
        try {
            // Clear VK session
            VKID.instance.logout()
            isSignedIn = false
        } catch (e: Exception) {
            // Log error but don't throw
        }
    }

    override fun isSignedIn(): Boolean {
        return isSignedIn
    }
}
