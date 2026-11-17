package com.taroshimaya.app.core.data.card

/**
 * Fake [CardRepository] (D2 sample, UY-07): serves the bundled sample catalog (28 cards, Rider-Waite).
 *
 * @param readCatalogJson returns the JSON text of the catalog; the app passes an asset reader so this
 * class stays free of Android types and is unit-testable.
 */
class FakeCardRepository(
    private val readCatalogJson: () -> String,
) : CardRepository {

    private val catalog: CardCatalog by lazy { CardCatalogParser.parse(readCatalogJson()) }

    override suspend fun getDecks(): List<Deck> = catalog.decks

    override suspend fun getDefaultDeck(): Deck =
        catalog.decks.firstOrNull { it.isDefault } ?: catalog.decks.first()

    override suspend fun getCards(): List<Card> = catalog.cards

    override suspend fun getCard(cardId: String): Card? = catalog.cards.firstOrNull { it.id == cardId }

    companion object {
        const val SAMPLE_ASSET_PATH = "cards/sample_cards.json"
    }
}
