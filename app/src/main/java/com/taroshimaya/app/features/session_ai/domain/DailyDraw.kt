package com.taroshimaya.app.features.session_ai.domain

/**
 * Daily 1-card draw entity (Collection 'daily_draws', D4 / F05 / F24 Glance Widget).
 * The document ID is deterministic: {userId}_{dateLocalKey} ensuring idempotency (DL04/DL05).
 */
data class DailyDraw(
    val id: String,
    val userId: String,
    val dateLocalKey: String, // YYYY-MM-DD in user's device timezone
    val deckId: String = "rider_waite",
    val cardId: String,
    val cardNameVi: String,
    val cardNameEn: String,
    val arcanaType: ArcanaType,
    val suit: CardSuit? = null,
    val isReversed: Boolean = false,
    val orientation: CardOrientation = if (isReversed) CardOrientation.REVERSED else CardOrientation.UPRIGHT,
    val dailyMessageVi: String,
    val dailyMessageEn: String,
    val streakCount: Int = 1,
    val coinReward: Int = 10,
    val coinTransactionId: String? = null,
    val shareImageUrl: String? = null,
    val drawnAtUtcIso: String,
    val createdAtUtcIso: String,
    val updatedAtUtcIso: String,
    val isDeleted: Boolean = false,
    val deletedAtUtcIso: String? = null,
)
