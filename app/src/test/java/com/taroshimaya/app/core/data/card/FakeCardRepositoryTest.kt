package com.taroshimaya.app.core.data.card

import com.taroshimaya.app.core.di.DefaultAppContainer
import java.io.File
import java.util.Locale
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeCardRepositoryTest {

    // Unit tests run with the module directory (app/) as working directory.
    private fun sampleJson() =
        File("src/main/assets/${FakeCardRepository.SAMPLE_ASSET_PATH}").readText(Charsets.UTF_8)

    private val repo = FakeCardRepository({ sampleJson() })

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
    fun getCards_returnsCanonicalOrder() = runTest {
        val ids = repo.getCards().map { it.id }
        val canonical = CardIds.all78()
        assertEquals(ids.sortedBy { canonical.indexOf(it) }, ids)
        assertEquals("major_00_fool", ids.first())
    }

    @Test
    fun getCards_sortsEvenWhenJsonIsShuffled() = runTest {
        val json = catalogJson(
            cardJson("minor_wands_01_ace", "minor", "wands", 1),
            cardJson("major_01_magician", "major", null, 1),
            cardJson("major_00_fool", "major", null, 0),
        )
        val shuffled = FakeCardRepository({ json })
        assertEquals(
            listOf("major_00_fool", "major_01_magician", "minor_wands_01_ace"),
            shuffled.getCards().map { it.id },
        )
    }

    @Test
    fun catalog_isReadOnlyOnce() = runTest {
        var reads = 0
        val counting = FakeCardRepository({ reads++; sampleJson() })
        counting.getCards()
        counting.getCard("major_00_fool")
        counting.getDecks()
        counting.getDefaultDeck()
        assertEquals(1, reads)
    }

    @Test
    fun imageUrl_unknownDeck_isNull() = runTest {
        val fool = repo.getCard("major_00_fool")!!
        assertNull(fool.imageUrl("unknown_deck"))
    }

    @Test
    fun keywords_forLanguage_picksEnglishOrFallsBackToVietnamese() = runTest {
        val kw = repo.getCard("major_00_fool")!!.keywordsUpright
        assertEquals(kw.en, kw.forLanguage("en"))
        assertEquals(kw.en, kw.forLanguage("EN-gb"))
        assertEquals(kw.vi, kw.forLanguage("vi-VN"))
        assertEquals(kw.vi, kw.forLanguage("fr"))
    }

    @Test
    fun cardIds_doNotDependOnDefaultLocale() {
        val original = Locale.getDefault()
        try {
            // Turkish (dotless i) and Arabic (non-ASCII digits with %02d) used to break ids.
            for (tag in listOf("tr-TR", "ar-EG", "fa-IR")) {
                Locale.setDefault(Locale.forLanguageTag(tag))
                assertEquals(tag, "major_09_hermit", CardIds.major(9))
                assertEquals(tag, "minor_pentacles_14_king", CardIds.minor(Suit.PENTACLES, 14))
            }
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun appContainer_cardRepository_isWiredWithAssetReader() = runTest {
        val container = DefaultAppContainer(readAsset = { File("src/main/assets/$it").readText(Charsets.UTF_8) })
        assertEquals("rider_waite", container.cardRepository.getDefaultDeck().id)
        assertEquals(repo.getCards().size, container.cardRepository.getCards().size)
    }

    @Test
    fun appContainer_withoutAssetReader_failsOnlyOnFirstCardCall() = runTest {
        val container = DefaultAppContainer()
        val cards = container.cardRepository // creating the repository itself must not throw
        val error = runCatching { cards.getCards() }.exceptionOrNull()
        assertTrue(error is IllegalStateException)
        assertTrue(error!!.message!!.contains("readAsset"))
    }

    @Test
    fun decks_haveDefaultWithCardBack() = runTest {
        val deck = repo.getDefaultDeck()
        assertEquals("rider_waite", deck.id)
        assertEquals("card_back_default", deck.backImage)
        assertEquals(listOf(deck), repo.getDecks())
    }
}
