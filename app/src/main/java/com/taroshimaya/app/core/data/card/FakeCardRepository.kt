package com.taroshimaya.app.core.data.card

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Fake [CardRepository] (D2 sample, UY-07): serves the bundled sample catalog (28 cards, Rider-Waite).
 *
 * Main-safe: the catalog is read and parsed once, on [ioDispatcher], the first time any function is
 * called. Later calls reuse the in-memory catalog; [getCard] is a map lookup.
 *
 * @param readCatalogJson returns the JSON text of the catalog; the app passes an asset reader so this
 * class stays free of Android types and is unit-testable.
 * @param ioDispatcher dispatcher used for the one-time read + parse (tests may pass their own).
 */
class FakeCardRepository(
    private val readCatalogJson: () -> String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CardRepository {

    private class Loaded(catalog: CardCatalog) {
        val decks: List<Deck> = catalog.decks

        /** Sorted in the canonical order promised by [CardRepository.getCards], whatever the JSON order. */
        val cards: List<Card> = catalog.cards.sortedBy { CANONICAL_ORDER[it.id] ?: Int.MAX_VALUE }

        val cardsById: Map<String, Card> = cards.associateBy { it.id }
    }

    private val loadLock = Mutex()

    @Volatile
    private var loaded: Loaded? = null

    private suspend fun catalog(): Loaded = loaded ?: loadLock.withLock {
        loaded ?: withContext(ioDispatcher) {
            Loaded(CardCatalogParser.parse(readCatalogJson()))
        }.also { loaded = it }
    }

    override suspend fun getDecks(): List<Deck> = catalog().decks

    override suspend fun getDefaultDeck(): Deck =
        catalog().decks.let { decks -> decks.firstOrNull { it.isDefault } ?: decks.first() }

    override suspend fun getCards(): List<Card> = catalog().cards

    override suspend fun getCard(cardId: String): Card? = catalog().cardsById[cardId]

    companion object {
        const val SAMPLE_ASSET_PATH = "cards/sample_cards.json"

        private val CANONICAL_ORDER: Map<String, Int> =
            CardIds.all78().withIndex().associate { (index, id) -> id to index }
    }
}
