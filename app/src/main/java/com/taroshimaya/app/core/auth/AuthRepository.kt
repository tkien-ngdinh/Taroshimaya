package com.taroshimaya.app.core.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for authentication and current user session (D1, owned by Uyên).
 *
 * Other packages depend only on this interface (via AppContainer.authRepository), never on a
 * concrete implementation. Phase 0–1 wires [FakeAuthRepository]; Phase 2 swaps in the Firebase version.
 */
interface AuthRepository {
    /** Current session. Always non-null in the Fake; a real implementation may emit null while loading. */
    val currentSession: StateFlow<UserSession?>

    /** Signs in as a fixed fake user of [role] (Fake) / starts the real sign-in flow later. */
    suspend fun signInWithRole(role: UserRole)

    /**
     * Role switch ("công tắc đổi 3 vai trò") for development and previews: changes the role of the
     * current session and keeps the sign-in state. Real implementations may restrict or ignore it.
     */
    suspend fun switchRole(role: UserRole)

    suspend fun signOut()
}
