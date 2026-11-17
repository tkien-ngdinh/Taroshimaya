package com.taroshimaya.app.core.data.card

/** Small JSON builders for parser/repository tests (same shape as assets/cards/sample_cards.json). */

internal fun deckJson(id: String = "rider_waite", isDefault: Boolean = true) = """
    {"id":"$id","nameVi":"Bộ $id","nameEn":"Deck $id","backImage":"card_back_default","isDefault":$isDefault}
""".trimIndent()

internal fun cardJson(id: String, arcana: String, suit: String?, number: Int) = """
    {
      "id":"$id","arcana":"$arcana","suit":${suit?.let { "\"$it\"" } ?: "null"},"number":$number,
      "nameVi":"Lá $id","nameEn":"Card $id","element":"air",
      "keywordsUpright":{"vi":["a"],"en":["a"]},
      "keywordsReversed":{"vi":["b"],"en":["b"]},
      "imageUrlByDeck":{"rider_waite":"https://example.test/$id.jpg"}
    }
""".trimIndent()

internal fun catalogJson(vararg cards: String, decks: List<String> = listOf(deckJson())) =
    """{"decks":[${decks.joinToString(",")}],"cards":[${cards.joinToString(",")}]}"""
