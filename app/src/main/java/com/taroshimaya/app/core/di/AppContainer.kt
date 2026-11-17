package com.taroshimaya.app.core.di

import com.taroshimaya.app.core.auth.AuthRepository
import com.taroshimaya.app.core.auth.FakeAuthRepository
import com.taroshimaya.app.core.data.DataSourceMode
import com.taroshimaya.app.core.data.LanguageManager
import com.taroshimaya.app.core.data.card.CardRepository
import com.taroshimaya.app.core.data.card.FakeCardRepository

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
    val cardRepository: CardRepository
}

class DefaultAppContainer(
    override val dataSourceMode: DataSourceMode = DataSourceMode.FAKE,
    /** Reads a bundled asset as text (e.g. `cards/sample_cards.json`); the Application passes AssetManager. */
    private val readAsset: (path: String) -> String = { error("No asset reader configured for '$it'") },
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

    override val cardRepository: CardRepository by lazy {
        when (dataSourceMode) {
            DataSourceMode.FAKE -> FakeCardRepository { readAsset(FakeCardRepository.SAMPLE_ASSET_PATH) }
            // Phase 2 (UY-12): wire FirestoreCardRepository here while keeping the CardRepository contract.
            DataSourceMode.FIREBASE -> FakeCardRepository { readAsset(FakeCardRepository.SAMPLE_ASSET_PATH) }
        }
    }
}
