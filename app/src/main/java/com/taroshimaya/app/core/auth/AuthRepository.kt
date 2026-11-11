package com.taroshimaya.app.core.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for authentication and current user session (D1, owned by Uyên).
 * Fake implementation is wired for Phase 0–1 via AppContainer (DI-01).
 */
interface AuthRepository {
    val currentSession: StateFlow<UserSession?>

    suspend fun signInWithRole(role: UserRole)

    suspend fun signOut()
}
