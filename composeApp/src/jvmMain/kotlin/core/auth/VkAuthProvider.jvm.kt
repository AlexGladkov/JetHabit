package core.auth

actual class VkAuthProvider : AuthProvider {
    override val providerName: String = "vk"

    override suspend fun signIn(): AuthResult {
        return AuthResult.Error("VK login is not supported on this platform")
    }

    override suspend fun signOut() {
        // No-op on unsupported platform
    }

    override fun isSignedIn(): Boolean {
        return false
    }
}
