package com.taroshimaya.app.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * In-memory Fake of [AuthRepository] (D1 sample, UY-06). No network, no Firebase.
 *
 * Each role maps to one fixed fake user, so uid/displayName are identical across runs and across
 * packages. Starts signed out as a guest with role [UserRole.USER].
 */
class FakeAuthRepository : AuthRepository {
    private val _currentSession = MutableStateFlow<UserSession?>(GUEST_SESSION)
    override val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    override suspend fun signInWithRole(role: UserRole) {
        _currentSession.value = fakeSessionFor(role)
    }

    override suspend fun switchRole(role: UserRole) {
        // A guest must never hold READER/ADMIN while isSignedIn = false (review UY-06, item 2).
        check(_currentSession.value?.isSignedIn == true) {
            "switchRole($role) requires a signed-in session; call signInWithRole() first."
        }
        _currentSession.value = fakeSessionFor(role)
    }

    override suspend fun signOut() {
        _currentSession.value = GUEST_SESSION
    }

    companion object {
        val GUEST_SESSION = UserSession(
            uid = "fake-guest",
            displayName = "Taroshimaya Guest",
            role = UserRole.USER,
            isSignedIn = false,
        )

        fun fakeSessionFor(role: UserRole): UserSession {
            // Locale.ROOT: ids must not depend on the device locale (e.g. Turkish dotless i).
            val key = role.name.lowercase(Locale.ROOT)
            return UserSession(
                uid = "fake-$key-001",
                displayName = "Fake ${key.replaceFirstChar { it.titlecase(Locale.ROOT) }}",
                role = role,
                isSignedIn = true,
                email = "$key@fake.taroshimaya.test",
            )
        }
    }
}
