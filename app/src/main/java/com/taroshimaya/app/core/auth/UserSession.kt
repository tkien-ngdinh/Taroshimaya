package com.taroshimaya.app.core.auth

/**
 * Core authenticated session model (D1).
 *
 * [uid] is stable per fake user so other packages can key data on it (e.g. readings, daily_draws).
 * [email] is null for the signed-out guest.
 */
data class UserSession(
    val uid: String,
    val displayName: String,
    val role: UserRole,
    val isSignedIn: Boolean,
    val email: String? = null,
)
