package com.taroshimaya.app.features.auth_deck.domain

/**
 * Package 1 contract stub (D2 Model Card/Deck & 78 cards, owned by Uyên · UY-07; D3 Birth Card, D8 Reader Role).
 * Core auth contract (D1) lives in [com.taroshimaya.app.core.auth.AuthRepository].
 */
interface DeckCatalogContract {
    fun availableDeckCount(): Int
}
