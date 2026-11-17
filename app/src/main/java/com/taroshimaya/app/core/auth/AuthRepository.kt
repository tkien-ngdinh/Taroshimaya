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
     * Role switch ("công tắc đổi 3 vai trò") for development and previews: replaces the current
     * signed-in session with the fixed fake user of [role]. Real implementations may restrict or ignore it.
     *
     * Requires a signed-in session: a guest never carries an elevated role.
     * Callers must still check [UserSession.isSignedIn] together with [UserSession.role] for access control.
     *
     * @throws IllegalStateException if no user is signed in. Call [signInWithRole] first.
     */
    suspend fun switchRole(role: UserRole)

    suspend fun signOut()
}
