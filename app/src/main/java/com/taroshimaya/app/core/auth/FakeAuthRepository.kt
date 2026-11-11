package com.taroshimaya.app.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Minimal in-memory Fake AuthRepository stub for v0 skeleton & Previews.
 * Expanded by Uyên in UY-06 (12/11) with full FakeAuth + 3-role toggle.
 */
class FakeAuthRepository : AuthRepository {
    private val _currentSession = MutableStateFlow<UserSession?>(
        UserSession(
            uid = "fake-user-001",
            displayName = "Taroshimaya Guest",
            role = UserRole.USER,
            isSignedIn = false,
        ),
    )
    override val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    override suspend fun signInWithRole(role: UserRole) {
        _currentSession.value = UserSession(
            uid = "fake-${role.name.lowercase()}-001",
            displayName = "Preview ${role.name}",
            role = role,
            isSignedIn = true,
        )
    }

    override suspend fun signOut() {
        _currentSession.value = UserSession(
            uid = "fake-guest",
            displayName = "Taroshimaya Guest",
            role = UserRole.USER,
            isSignedIn = false,
        )
    }
}
