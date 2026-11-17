package com.taroshimaya.app.core.data.card

import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeCardRepositoryTest {

    // Unit tests run with the module directory (app/) as working directory.
    private val repo = FakeCardRepository {
        File("src/main/assets/${FakeCardRepository.SAMPLE_ASSET_PATH}").readText(Charsets.UTF_8)
    }

    @Test
    fun cardIds_all78_matchContractConvention() {
        val ids = CardIds.all78()
        assertEquals(78, ids.size)
        assertEquals(78, ids.toSet().size)
        assertEquals("major_00_fool", ids.first())
        assertTrue("major_21_world" in ids)
        assertTrue("minor_wands_01_ace" in ids)
        assertEquals("minor_pentacles_14_king", ids.last())
        assertTrue(ids.all { id -> id.all { it.isLowerCase() || it.isDigit() || it == '_' } })
    }

    @Test
    fun sampleCatalog_has20to30CardsWithValidUniqueIds() = runTest {
        val cards = repo.getCards()
        assertTrue("sample must have 20–30 cards, was ${cards.size}", cards.size in 20..30)
        val valid = CardIds.all78().toSet()
        assertTrue(cards.all { it.id in valid })
        assertEquals(cards.size, cards.map { it.id }.toSet().size)
    }

    @Test
    fun sampleCatalog_everyCardIsComplete() = runTest {
        val deckId = repo.getDefaultDeck().id
        for (card in repo.getCards()) {
            assertTrue(card.id, card.name.vi.isNotBlank() && card.name.en.isNotBlank())
            assertTrue(card.id, card.keywordsUpright.vi.isNotEmpty() && card.keywordsUpright.en.isNotEmpty())
            assertTrue(card.id, card.keywordsReversed.vi.isNotEmpty() && card.keywordsReversed.en.isNotEmpty())
            assertTrue(card.id, card.imageUrl(deckId)?.startsWith("https://") == true)
            assertEquals(card.id, card.suit == null, card.arcana == Arcana.MAJOR)
        }
    }

    @Test
    fun sampleCatalog_idsMatchNumberAndSuit() = runTest {
        for (card in repo.getCards()) {
            val expected = if (card.arcana == Arcana.MAJOR) {
                CardIds.major(card.number)
            } else {
                CardIds.minor(card.suit!!, card.number)
            }
            assertEquals(expected, card.id)
        }
    }

    @Test
    fun getCard_returnsCardOrNull() = runTest {
        val fool = repo.getCard("major_00_fool")
        assertNotNull(fool)
        assertEquals("The Fool", fool!!.name.en)
        assertEquals("Kẻ Khờ", fool.name.vi)
        assertEquals("Kẻ Khờ", fool.name.forLanguage("vi"))
        assertEquals("The Fool", fool.name.forLanguage("en-US"))
        assertNull(repo.getCard("does_not_exist"))
    }

    @Test
    fun decks_haveDefaultWithCardBack() = runTest {
        val deck = repo.getDefaultDeck()
        assertEquals("rider_waite", deck.id)
        assertEquals("card_back_default", deck.backImage)
        assertEquals(listOf(deck), repo.getDecks())
    }
}
