package com.taroshimaya.app.core.data.card

import java.util.Locale

/**
 * D2 core models (owned by Uyên): one tarot card and one deck. See D2 contract in the project doc
 * (cardId convention: `major_00_fool`..`major_21_world`, `minor_wands_01_ace`..`minor_pentacles_14_king`).
 */
enum class Arcana { MAJOR, MINOR }

enum class Suit { WANDS, CUPS, SWORDS, PENTACLES }

enum class Element { FIRE, WATER, AIR, EARTH }

/** Text in the two supported UI languages (C19). */
data class LocalizedText(val vi: String, val en: String) {
    fun forLanguage(languageTag: String): String =
        if (languageTag.startsWith("en", ignoreCase = true)) en else vi
}

data class LocalizedKeywords(val vi: List<String>, val en: List<String>) {
    fun forLanguage(languageTag: String): List<String> =
        if (languageTag.startsWith("en", ignoreCase = true)) en else vi
}

data class Card(
    /** Standard lowercase ASCII id, e.g. `major_00_fool`. */
    val id: String,
    val arcana: Arcana,
    /** Null for major arcana. */
    val suit: Suit?,
    /** Major: 0–21. Minor: 1–14 (1 ace, 2–10 pips, 11 page, 12 knight, 13 queen, 14 king). */
    val number: Int,
    val name: LocalizedText,
    val element: Element,
    val keywordsUpright: LocalizedKeywords,
    val keywordsReversed: LocalizedKeywords,
    /** Face image URL keyed by deckId. A deck without an entry has no image for this card yet. */
    val imageUrlByDeck: Map<String, String>,
) {
    fun imageUrl(deckId: String): String? = imageUrlByDeck[deckId]
}

data class Deck(
    val id: String,
    val name: LocalizedText,
    /**
     * Card back image: the name of a drawable resource bundled with the app (e.g. `card_back_default`)
     * in Phase 0–1, or an https URL once the real decks are served from storage.
     */
    val backImage: String,
    val isDefault: Boolean,
)

/** Builds the standard 78 card ids so every package derives ids the same way. */
object CardIds {
    private val MAJOR = listOf(
        "fool", "magician", "high_priestess", "empress", "emperor", "hierophant", "lovers",
        "chariot", "strength", "hermit", "wheel_of_fortune", "justice", "hanged_man", "death",
        "temperance", "devil", "tower", "star", "moon", "sun", "judgement", "world",
    )
    private val RANKS = listOf(
        "ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "page", "knight", "queen", "king",
    )

    fun major(number: Int): String {
        require(number in MAJOR.indices) { "Major arcana number must be 0..21, was $number" }
        return "major_${twoDigits(number)}_${MAJOR[number]}"
    }

    fun minor(suit: Suit, number: Int): String {
        require(number in 1..RANKS.size) { "Minor arcana number must be 1..14, was $number" }
        return "minor_${suit.name.lowercase(Locale.ROOT)}_${twoDigits(number)}_${RANKS[number - 1]}"
    }

    /**
     * ASCII zero-padding without String.format, so ids never pick up the device locale
     * (e.g. Arabic/Persian digits from "%02d", Turkish dotless i from lowercase()).
     */
    private fun twoDigits(n: Int): String = n.toString().padStart(2, '0')

    /** All 78 ids: 22 major then wands, cups, swords, pentacles (14 each). */
    fun all78(): List<String> =
        MAJOR.indices.map(::major) + Suit.entries.flatMap { suit -> (1..14).map { minor(suit, it) } }
}
