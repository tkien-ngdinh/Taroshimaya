package com.taroshimaya.app.core.auth

/**
 * Core authenticated session model (D1 contract starter for UY-06).
 */
data class UserSession(
    val uid: String,
    val displayName: String,
    val role: UserRole,
    val isSignedIn: Boolean,
)
