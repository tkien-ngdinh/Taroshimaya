package com.taroshimaya.app.core.data.card

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CardCatalogParserTest {

    private val fool = cardJson("major_00_fool", "major", null, 0)
    private val aceOfWands = cardJson("minor_wands_01_ace", "minor", "wands", 1)

    @Test
    fun parse_validCatalog() {
        val catalog = CardCatalogParser.parse(catalogJson(fool, aceOfWands))
        assertEquals(1, catalog.decks.size)
        assertEquals(2, catalog.cards.size)
        assertNull(catalog.cards[0].suit)
        assertEquals(Suit.WANDS, catalog.cards[1].suit)
    }

    @Test
    fun parse_rejectsDuplicateDeckId() {
        val json = catalogJson(fool, decks = listOf(deckJson("rider_waite"), deckJson("rider_waite", isDefault = false)))
        assertFailsWith("Duplicate deck id") { CardCatalogParser.parse(json) }
    }

    @Test
    fun parse_rejectsMoreThanOneDefaultDeck() {
        val json = catalogJson(fool, decks = listOf(deckJson("a"), deckJson("b")))
        assertFailsWith("more than one default deck") { CardCatalogParser.parse(json) }
    }

    @Test
    fun parse_rejectsDuplicateCardId() {
        assertFailsWith("Duplicate card id") { CardCatalogParser.parse(catalogJson(fool, fool)) }
    }

    @Test
    fun parse_rejectsMajorWithSuit() {
        val bad = cardJson("major_00_fool", "major", "wands", 0)
        assertFailsWith("major arcana must have no suit") { CardCatalogParser.parse(catalogJson(bad)) }
    }

    @Test
    fun parse_rejectsMinorWithoutSuit() {
        val bad = cardJson("minor_wands_01_ace", "minor", null, 1)
        assertFailsWith("minor arcana must have one") { CardCatalogParser.parse(catalogJson(bad)) }
    }

    @Test
    fun parse_rejectsEmptyDecks() {
        val json = catalogJson(fool, decks = emptyList())
        assertFailsWith("no deck") { CardCatalogParser.parse(json) }
    }

    @Test
    fun parse_rejectsUnknownEnumValue() {
        val bad = cardJson("minor_wands_01_ace", "minor", "coins", 1)
        assertThrows(IllegalArgumentException::class.java) { CardCatalogParser.parse(catalogJson(bad)) }
    }

    private fun assertFailsWith(messagePart: String, block: () -> Unit) {
        val e = assertThrows(IllegalArgumentException::class.java) { block() }
        assertTrue("'${e.message}' should contain '$messagePart'", e.message!!.contains(messagePart))
    }
}
