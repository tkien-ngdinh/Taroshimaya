package com.taroshimaya.app.core.data.card

import org.json.JSONArray
import org.json.JSONObject

data class CardCatalog(val decks: List<Deck>, val cards: List<Card>)

/** Parses the D2 sample JSON (`assets/cards/sample_cards.json`) into models. */
object CardCatalogParser {

    fun parse(json: String): CardCatalog {
        val root = JSONObject(json)
        val decks = root.getJSONArray("decks").objects().map(::parseDeck)
        val cards = root.getJSONArray("cards").objects().map(::parseCard)
        require(decks.isNotEmpty()) { "Catalog has no deck" }
        require(cards.map { it.id }.toSet().size == cards.size) { "Duplicate card id in catalog" }
        return CardCatalog(decks, cards)
    }

    private fun parseDeck(o: JSONObject) = Deck(
        id = o.getString("id"),
        name = LocalizedText(vi = o.getString("nameVi"), en = o.getString("nameEn")),
        backImage = o.getString("backImage"),
        isDefault = o.optBoolean("isDefault", false),
    )

    private fun parseCard(o: JSONObject): Card {
        val arcana = Arcana.valueOf(o.getString("arcana").uppercase())
        val suit = if (o.isNull("suit")) null else Suit.valueOf(o.getString("suit").uppercase())
        return Card(
            id = o.getString("id"),
            arcana = arcana,
            suit = suit,
            number = o.getInt("number"),
            name = LocalizedText(vi = o.getString("nameVi"), en = o.getString("nameEn")),
            element = Element.valueOf(o.getString("element").uppercase()),
            keywordsUpright = parseKeywords(o.getJSONObject("keywordsUpright")),
            keywordsReversed = parseKeywords(o.getJSONObject("keywordsReversed")),
            imageUrlByDeck = o.getJSONObject("imageUrlByDeck").let { m ->
                m.keys().asSequence().associateWith { m.getString(it) }
            },
        )
    }

    private fun parseKeywords(o: JSONObject) = LocalizedKeywords(
        vi = o.getJSONArray("vi").strings(),
        en = o.getJSONArray("en").strings(),
    )

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }

    private fun JSONArray.strings(): List<String> = (0 until length()).map { getString(it) }
}
