package com.taroshimaya.app.features.auth_deck.data

import com.taroshimaya.app.features.auth_deck.domain.DeckCatalogContract

/**
 * Phase 0–1 Fake starter implementation for Package 1 (expanded in UY-07, 12/11).
 */
class FakeAuthDeckDataSource : DeckCatalogContract {
    override fun availableDeckCount(): Int = 3
}
