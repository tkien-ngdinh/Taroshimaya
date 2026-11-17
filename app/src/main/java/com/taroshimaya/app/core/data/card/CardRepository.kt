package com.taroshimaya.app.core.data.card

/**
 * D2 contract: read access to decks and cards (owned by Uyên). Other packages never read the
 * `cards` / `decks` collections directly. The Fake serves the bundled sample JSON; the real
 * implementation (UY-12) will read Firestore behind the same interface.
 */
interface CardRepository {
    suspend fun getDecks(): List<Deck>

    suspend fun getDefaultDeck(): Deck

    /** Cards of the catalog, in id order (major 0–21 then minor by suit). */
    suspend fun getCards(): List<Card>

    suspend fun getCard(cardId: String): Card?
}
