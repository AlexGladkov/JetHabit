package core.auth

sealed class AuthResult {
    data class Success(
        val userId: String,
        val displayName: String,
        val email: String?,
        val avatarUrl: String?
    ) : AuthResult()

    data class Error(val message: String) : AuthResult()

    data object Cancelled : AuthResult()
}
