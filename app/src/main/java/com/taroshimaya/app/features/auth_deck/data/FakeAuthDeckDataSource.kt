package com.taroshimaya.app.features.auth_deck.data

import com.taroshimaya.app.features.auth_deck.domain.DeckCatalogContract

/**
 * Phase 0–1 Fake starter for Package 1 (v0 skeleton).
 *
 * Real deck/card data lives in D2 ([com.taroshimaya.app.core.data.card.CardRepository], UY-07);
 * new code should use `AppContainer.cardRepository`. The count here mirrors the D2 sample catalog,
 * which bundles a single deck (`rider_waite`).
 */
class FakeAuthDeckDataSource : DeckCatalogContract {
    override fun availableDeckCount(): Int = 1
}
