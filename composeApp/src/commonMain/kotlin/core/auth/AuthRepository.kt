package core.auth

import core.database.dao.UserProfileDao
import core.database.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val authProvider: AuthProvider,
    private val userProfileDao: UserProfileDao
) {
    fun getAuthState(): Flow<Boolean> {
        return userProfileDao.getUserProfile().map { profile ->
            profile?.authProvider != null
        }
    }

    fun getCurrentProvider(): String? {
        return if (authProvider.isSignedIn()) authProvider.providerName else null
    }

    suspend fun signIn(): AuthResult {
        val result = authProvider.signIn()

        if (result is AuthResult.Success) {
            // Save user profile to database
            val userProfile = UserProfile(
                id = 1,
                name = result.displayName,
                email = result.email ?: "",
                phoneNumber = "",
                avatarUri = result.avatarUrl,
                vkId = result.userId,
                authProvider = authProvider.providerName
            )
            userProfileDao.insertOrUpdateProfile(userProfile)
        }

        return result
    }

    suspend fun signOut() {
        authProvider.signOut()
        userProfileDao.clearProfile()
    }

    fun isSignedIn(): Boolean {
        return authProvider.isSignedIn()
    }
}
