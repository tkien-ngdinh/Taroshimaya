package com.taroshimaya.app.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        val current = _currentSession.value
        _currentSession.value = if (current?.isSignedIn == true) {
            fakeSessionFor(role)
        } else {
            // Signed out: only the role of the guest changes, so screens can be previewed per role.
            (current ?: GUEST_SESSION).copy(role = role)
        }
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
            val key = role.name.lowercase()
            return UserSession(
                uid = "fake-$key-001",
                displayName = "Fake ${role.name.lowercase().replaceFirstChar { it.uppercase() }}",
                role = role,
                isSignedIn = true,
                email = "$key@fake.taroshimaya.test",
            )
        }
    }
}
