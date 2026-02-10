package core.auth

interface AuthProvider {
    val providerName: String
    suspend fun signIn(): AuthResult
    suspend fun signOut()
    fun isSignedIn(): Boolean
}
