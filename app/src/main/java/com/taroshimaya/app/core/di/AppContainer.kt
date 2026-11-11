package com.taroshimaya.app.core.di

import com.taroshimaya.app.core.auth.AuthRepository
import com.taroshimaya.app.core.auth.FakeAuthRepository
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.data.LanguageManager

/**
 * Manual dependency injection container initialized at application startup (DI-01).
 *
 * Selects Fake implementations (Phase 0–1) or Firebase implementations (Phase 2+) once at startup
 * so UI Composables never branch on [dataSourceMode].
 */
interface AppContainer {
    val dataSourceMode: DataSourceMode
    val authRepository: AuthRepository
    val languageManager: LanguageManager
}

class DefaultAppContainer(
    override val dataSourceMode: DataSourceMode = DataSourceMode.FAKE,
) : AppContainer {
    override val languageManager: LanguageManager by lazy {
        LanguageManager()
    }

    override val authRepository: AuthRepository by lazy {
        when (dataSourceMode) {
            DataSourceMode.FAKE -> FakeAuthRepository()
            // Phase 2 (UY-15 / QU-01): wire FirebaseAuthRepository here while keeping AuthRepository contract.
            DataSourceMode.FIREBASE -> FakeAuthRepository()
        }
    }
}
